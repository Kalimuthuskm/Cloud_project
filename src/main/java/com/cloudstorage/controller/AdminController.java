// src/main/java/com/cloudstorage/controller/AdminController.java

package com.cloudstorage.controller;

import com.cloudstorage.model.FileMetadata;
import com.cloudstorage.repository.FileMetadataRepository;
import com.cloudstorage.repository.UserRepository;
import com.cloudstorage.repository.BackupRepository;
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
    private final BackupRepository backupRepo;
    private final FileMetadataRepository metadataRepo;
    private final UserRepository userRepository;

    @GetMapping("/all-files")
    @PreAuthorize("hasRole('ADMIN')")
    public List<FileMetadata> getAllFilesUploadedByAllUsers() {
        return metadataRepo.findAll();
    }

    @DeleteMapping("/delete/{filename}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFileByAdmin(@PathVariable String filename) {
        try {
            fileService.deleteFile(filename);
            return ResponseEntity.ok("Admin deleted file: " + filename);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting file: " + e.getMessage());
        }
    }

    @GetMapping("/user-files/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getFilesByUser(@PathVariable String username) {
        try {
            List<FileMetadata> files = metadataRepo.findByUploadedBy(username);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching files: " + e.getMessage());
        }
    }

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminStats() {
        try {
            long totalFiles = metadataRepo.count();
            long totalUsers = userRepository.count(); // Ensure it's injected correctly
            long totalBackups = backupRepo.count(); // Ensure you have a backupRepo
            
            Map<String, Long> stats = new HashMap<>();
            stats.put("totalFiles", totalFiles);
            stats.put("totalUsers", totalUsers);
            stats.put("totalBackups", totalBackups);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching stats: " + e.getMessage());
        }
    }
}
