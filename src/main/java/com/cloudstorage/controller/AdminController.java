package com.cloudstorage.controller;

import com.cloudstorage.model.FileMetadata;
import com.cloudstorage.repository.BlockchainLedgerRepository;
import com.cloudstorage.repository.FileMetadataRepository;
import com.cloudstorage.repository.UserRepository;
import com.cloudstorage.repository.FileBackupRepository;
import com.cloudstorage.service.FileService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.cloudstorage.model.BlockchainLedger;
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

    // ✅ 1. Admin - View all files uploaded by any user
    @GetMapping("/all-files")
    @PreAuthorize("hasRole('ADMIN')")
    public List<FileMetadata> getAllFilesUploadedByAllUsers() {
        return metadataRepo.findAll();
    }

    // ✅ 2. Admin - Delete file

    @DeleteMapping("/admin/delete-file/{fileId}")
    public ResponseEntity<String> deleteFileAsAdmin(@PathVariable Long fileId) {
        fileService.deleteFileById(fileId);
        return ResponseEntity.ok("File deleted successfully (ID: " + fileId + ")");
    }
    


    // ✅ 3. Admin - Get files uploaded by a specific user
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

    // ✅ 4. Admin - Dashboard stats
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminStats() {
        try {
            long totalFiles = metadataRepo.count();
            long totalUsers = userRepository.count();  // Ensure you have autowired UserRepository
            long totalBackups = backupRepo.count();  // Make sure you have a backupRepo
    
            // Log the values for debugging
            System.out.println("Total Files: " + totalFiles);
            System.out.println("Total Users: " + totalUsers);
            System.out.println("Total Backups: " + totalBackups);
    
            Map<String, Long> stats = new HashMap<>();
            stats.put("totalFiles", totalFiles);
            stats.put("totalUsers", totalUsers);
            stats.put("totalBackups", totalBackups);
    
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();  // Log the full exception for better debugging
            return ResponseEntity.status(500).body("Error fetching admin stats: " + e.getMessage());
        }
    }
    

    // ✅ 5. Admin - Get Blockchain logs
    @GetMapping("/blockchain")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BlockchainLedger>> getBlockchainLogs() {
        try {
            List<BlockchainLedger> logs = ledgerRepo.findAll();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
