package com.cloudstorage.controller;

import com.cloudstorage.model.FileMetadata;
import com.cloudstorage.model.BlockchainLedger;
import com.cloudstorage.repository.*;
import com.cloudstorage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final FileService fileService;
    private final FileMetadataRepository metadataRepo;
    private final UserRepository userRepository;
    private final FileBackupRepository backupRepo;
    private final BlockchainLedgerRepository ledgerRepo;

    // ✅ 1. Get all files uploaded by all users
    @GetMapping("/all-files")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FileMetadata>> getAllFilesUploadedByAllUsers() {
        return ResponseEntity.ok(metadataRepo.findAll());
    }

    // ✅ 2. Delete a file by its ID
    @DeleteMapping("/delete/{fileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFileByAdmin(@PathVariable Long fileId) {
        fileService.deleteFileById(fileId);
        return ResponseEntity.ok("✅ File deleted successfully (ID: " + fileId + ")");
    }

    // ✅ 3. Get files by a specific user
    @GetMapping("/user-files/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getFilesByUser(@PathVariable String username) {
        try {
            List<FileMetadata> files = metadataRepo.findByUploadedBy(username);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error fetching files: " + e.getMessage());
        }
    }

    // ✅ 4. Admin dashboard stats
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminStats() {
        try {
            long totalFiles = metadataRepo.count();
            long totalUsers = userRepository.count();
            long totalBackups = backupRepo.count();

            Map<String, Long> stats = new HashMap<>();
            stats.put("totalFiles", totalFiles);
            stats.put("totalUsers", totalUsers);
            stats.put("totalBackups", totalBackups);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Error fetching stats: " + e.getMessage());
        }
    }

    // ✅ 5. Blockchain logs for audit
    @GetMapping("/blockchain")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBlockchainLogs() {
        try {
            List<BlockchainLedger> logs = ledgerRepo.findAll();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error fetching logs: " + e.getMessage());
        }
    }
}
