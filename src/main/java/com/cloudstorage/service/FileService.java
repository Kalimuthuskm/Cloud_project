package com.cloudstorage.service;

import com.cloudstorage.model.*;
import com.cloudstorage.repository.*;
import com.cloudstorage.util.AESUtil;
import com.cloudstorage.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMetadataRepository metadataRepo;
    private final CloudChunk1Repository chunk1Repo;
    private final CloudChunk2Repository chunk2Repo;
    private final FileBackupRepository backupRepo;


    public FileMetadata uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Uploaded file is empty or null");
        }
    
        System.out.println("✅ Received file: " + file.getOriginalFilename()); // ✅ Debug log
    
        byte[] fileBytes = file.getBytes();
        int mid = fileBytes.length / 2;
        byte[] chunk1 = Arrays.copyOfRange(fileBytes, 0, mid);
        byte[] chunk2 = Arrays.copyOfRange(fileBytes, mid, fileBytes.length);
    
        String originalName = file.getOriginalFilename();
    
        chunk1Repo.save(CloudChunk1.builder().fileName(originalName).chunkData(chunk1).build());
        chunk2Repo.save(CloudChunk2.builder().fileName(originalName).chunkData(chunk2).build());
    
        try {
            byte[] encrypted = AESUtil.encrypt(fileBytes);
            backupRepo.save(FileBackup.builder().fileName(originalName).encryptedData(encrypted).build());
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    
        return metadataRepo.save(FileMetadata.builder()
                .filename(originalName)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .uploadTime(LocalDateTime.now())
                .build());
    }
    

    public List<FileMetadata> getAllFiles() {
        return metadataRepo.findAll();
    }

    public byte[] downloadFile(String filename) throws Exception {
        CloudChunk1 chunk1 = chunk1Repo.findAll().stream()
                .filter(c -> c.getFileName().equals(filename))
                .findFirst().orElseThrow(() -> new RuntimeException("Chunk 1 not found"));

        CloudChunk2 chunk2 = chunk2Repo.findAll().stream()
                .filter(c -> c.getFileName().equals(filename))
                .findFirst().orElseThrow(() -> new RuntimeException("Chunk 2 not found"));

        return FileUtil.mergeChunks(chunk1.getChunkData(), chunk2.getChunkData());
    }

    public byte[] downloadFromBackup(String filename) throws Exception {
        FileBackup backup = backupRepo.findAll().stream()
                .filter(b -> b.getFileName().equals(filename))
                .findFirst().orElseThrow(() -> new RuntimeException("Backup not found"));

        return AESUtil.decrypt(backup.getEncryptedData());
    }

    public void deleteFile(String filename) {
        metadataRepo.deleteAll(metadataRepo.findAll().stream()
                .filter(f -> f.getFilename().equals(filename)).toList());

        chunk1Repo.deleteAll(chunk1Repo.findAll().stream()
                .filter(c -> c.getFileName().equals(filename)).toList());

        chunk2Repo.deleteAll(chunk2Repo.findAll().stream()
                .filter(c -> c.getFileName().equals(filename)).toList());

        backupRepo.deleteAll(backupRepo.findAll().stream()
                .filter(b -> b.getFileName().equals(filename)).toList());
    }
}