package com.cloudstorage.controller;

import com.cloudstorage.model.BlockchainLedger;
import com.cloudstorage.model.FileMetadata;
import com.cloudstorage.repository.BlockchainLedgerRepository;
import com.cloudstorage.repository.FileMetadataRepository;
import com.cloudstorage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileMetadataRepository metadataRepo;
    private final BlockchainLedgerRepository ledgerRepo;

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @PostMapping("/upload")
    public ResponseEntity<?> upload(MultipartFile file, Authentication authentication) {
        try {
            logger.info("File upload request received - Name: {}, Size: {}, Type: {}",
                    file.getOriginalFilename(),
                    file.getSize(),
                    file.getContentType());

            if (file.isEmpty()) {
                logger.warn("Empty file received");
                return ResponseEntity.badRequest().body("File cannot be empty");
            }

            String username = authentication.getName();
            FileMetadata metadata = fileService.uploadFile(file, username);
            return ResponseEntity.ok(metadata);

        } catch (Exception e) {
            logger.error("File upload failed", e);
            return ResponseEntity.internalServerError()
                    .body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<FileMetadata>> getUserFiles(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(fileService.getFilesByUser(username));
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable String filename, Authentication authentication) throws Exception {
        String username = authentication.getName();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(fileService.downloadFileForUser(filename, username));
    }

    @DeleteMapping("/delete/{filename}")
    public ResponseEntity<String> delete(@PathVariable String filename, Authentication authentication) {
        String username = authentication.getName();
        fileService.deleteFileForUser(filename, username);
        return ResponseEntity.ok("File deleted: " + filename);
    }

    // ✅ Admin: Get all uploaded files
    @GetMapping("/admin/all-files")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FileMetadata>> getAllFilesForAdmin() {
        return ResponseEntity.ok(metadataRepo.findAll());
    }

    // ✅ Admin: Delete any file
    @DeleteMapping("/admin/delete-file/{filename}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteFileByAdmin(@PathVariable String filename) {
        fileService.deleteFileById(Long.parseLong(filename));
        return ResponseEntity.ok("✅ File deleted by admin: " + filename);
    }

    // ✅ Admin: Blockchain logs
    @GetMapping("/admin/blockchain-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BlockchainLedger>> getBlockchainLogs() {
        return ResponseEntity.ok(ledgerRepo.findAll());
    }
}