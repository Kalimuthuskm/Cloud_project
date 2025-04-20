package com.cloudstorage.controller;

import com.cloudstorage.model.FileMetadata;
import com.cloudstorage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            logger.info("File upload request received - Name: {}, Size: {}, Type: {}",
                    file.getOriginalFilename(),
                    file.getSize(),
                    file.getContentType());

            if (file.isEmpty()) {
                logger.warn("Empty file received");
                return ResponseEntity.badRequest().body("File cannot be empty");
            }

            FileMetadata metadata = fileService.uploadFile(file);
            return ResponseEntity.ok(metadata);

        } catch (Exception e) {
            logger.error("File upload failed", e);
            return ResponseEntity.internalServerError()
                    .body("File upload failed: " + e.getMessage());
        }
    }

    // ✅ Keep ONLY this for showing current user's uploaded files
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
}
