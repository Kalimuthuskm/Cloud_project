package com.cloudstorage.service;

import com.cloudstorage.model.*;
import com.cloudstorage.repository.*;
import com.cloudstorage.util.AESUtil;
import com.cloudstorage.util.FileUtil;
import com.cloudstorage.util.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileMetadataRepository metadataRepo;
    private final CloudChunk1Repository chunk1Repo;
    private final CloudChunk2Repository chunk2Repo;
    private final FileBackupRepository backupRepo;
    private final ChunkMetadataRepository chunkMetadataRepo;
    private final BlockchainLedgerRepository ledgerRepo;
    private final UserRepository userRepository;

    public FileMetadata uploadFile(MultipartFile file, String folderName) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Uploaded file is empty or null");
        }   
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Uploaded file is empty or null");
        }

        String originalName = Paths.get(file.getOriginalFilename()).getFileName().toString();
        log.info("✅ Received file: {}", originalName);

        byte[] fileBytes = file.getBytes();
        int mid = fileBytes.length / 2;
        byte[] chunk1 = FileUtil.slice(fileBytes, 0, mid);
        byte[] chunk2 = FileUtil.slice(fileBytes, mid, fileBytes.length);

        String uploadedBy = getCurrentUsername();
        LocalDateTime timestamp = LocalDateTime.now();

        try {
            byte[] encryptedChunk1 = AESUtil.encrypt(chunk1);
            byte[] encryptedChunk2 = AESUtil.encrypt(chunk2);
            byte[] encryptedFullFile = AESUtil.encrypt(fileBytes);

            chunk1Repo.save(CloudChunk1.builder()
                    .fileName(originalName)
                    .chunkData(encryptedChunk1)
                    .build());

            chunk2Repo.save(CloudChunk2.builder()
                    .fileName(originalName)
                    .chunkData(encryptedChunk2)
                    .build());

            backupRepo.save(FileBackup.builder()
                    .fileName(originalName)
                    .encryptedData(encryptedFullFile)
                    .build());

            chunkMetadataRepo.save(createChunkMetadata("chunk1", originalName, chunk1.length, uploadedBy, timestamp));
            chunkMetadataRepo.save(createChunkMetadata("chunk2", originalName, chunk2.length, uploadedBy, timestamp));

            ledgerRepo.save(BlockchainLedger.builder()
                    .fileName(originalName)
                    // Removed invalid method call
                    .performedBy(uploadedBy)
                    .timestamp(timestamp)
                    .hash(HashUtil.sha256(originalName + uploadedBy + timestamp))
                    .build());

        } catch (Exception e) {
            log.error("Encryption failed for file: {}", originalName, e);
            throw new RuntimeException("Encryption failed", e);
        }

        // ✅ FIX: Use effectiveFolder to avoid variable conflict
        String effectiveFolder = (folderName != null && !folderName.isBlank()) ? folderName : "default";

        FileMetadata metadata = FileMetadata.builder()
                .filename(originalName)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .uploadTime(timestamp)
                .uploadedBy(uploadedBy)
                .folderName(effectiveFolder)
                .build();

        return metadataRepo.save(metadata);
    }

    public List<FileMetadata> getAllFiles() {
        return metadataRepo.findByUploadedBy(getCurrentUsername());
    }

    public List<FileMetadata> getFilesByUser(String username) {
        return metadataRepo.findByUploadedBy(username);
    }

    public byte[] downloadFileForUser(String filename, String username) throws Exception {
        Optional<FileMetadata> fileMetadata = metadataRepo.findByFilenameAndUploadedBy(filename, username);
        fileMetadata.orElseThrow(() -> new RuntimeException("File not found or unauthorized"));
        return downloadFile(filename);
    }

    public byte[] downloadFile(String filename) throws Exception {
        CloudChunk1 chunk1 = chunk1Repo.findByFileName(filename)
                .orElseThrow(() -> new RuntimeException("Chunk 1 not found"));
        CloudChunk2 chunk2 = chunk2Repo.findByFileName(filename)
                .orElseThrow(() -> new RuntimeException("Chunk 2 not found"));

        byte[] decryptedChunk1 = AESUtil.decrypt(chunk1.getChunkData());
        byte[] decryptedChunk2 = AESUtil.decrypt(chunk2.getChunkData());

        return FileUtil.mergeChunks(decryptedChunk1, decryptedChunk2);
    }

    public byte[] downloadFromBackup(String filename) throws Exception {
        FileBackup backup = backupRepo.findByFileName(filename)
                .orElseThrow(() -> new RuntimeException("Backup not found"));
        return AESUtil.decrypt(backup.getEncryptedData());
    }

    public void deleteFileForUser(String filename, String username) {
        Optional<FileMetadata> fileMetadata = metadataRepo.findByFilenameAndUploadedBy(filename, username);
        fileMetadata.orElseThrow(() -> new RuntimeException("File not found or unauthorized"));
        deleteFileById(filename);
    }
    public void deleteFileById(Long fileId) {
        FileMetadata metadata = metadataRepo.findById(fileId)
            .orElseThrow(() -> new RuntimeException("File not found with ID: " + fileId));
    
        String filename = metadata.getFilename();
    
        // Delete from all relevant tables
        chunk1Repo.deleteByFilename(filename);
        chunk2Repo.deleteByFilename(filename);
        backupFileRepo.deleteByFilename(filename);
        chunkMetadataRepo.deleteByFileName(filename);
        fileMetadataRepo.deleteById(fileId);
    
        log.info("🗑️ Deleted all file data for ID {} (filename: {})", fileId, filename);
    }
    

    public List<String> getDeletedFiles(String username) {
        List<String> backups = backupRepo.findAll().stream()
                .filter(b -> b.getFileName().contains(username))
                .map(FileBackup::getFileName)
                .toList();

        List<String> existing = metadataRepo.findByUploadedBy(username)
                .stream().map(FileMetadata::getFilename).toList();

        return backups.stream()
                .filter(f -> !existing.contains(f))
                .toList();
    }

    public void restoreFile(String filename, String username) throws Exception {
        byte[] decrypted = downloadFromBackup(filename);

        MultipartFile multipart = FileUtil.createMultipartFileFromBytes(decrypted, filename);
        uploadFile(multipart, "Recovered");
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private ChunkMetadata createChunkMetadata(String chunkName, String fileName, int size, String uploadedBy, LocalDateTime timestamp) {
        return ChunkMetadata.builder()
                .chunkName(chunkName)
                .fileName(fileName)
                .chunkSize(size)
                .encrypted(true)
                .timestamp(timestamp)
                .uploadedBy(uploadedBy)
                .build();
    }
}
