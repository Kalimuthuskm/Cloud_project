package com.cloudstorage.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import com.cloudstorage.model.FileMetadata; // Ensure this is the correct package for FileMetadata
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cloudstorage.service.FileService; // Ensure this is the correct package for FileService

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
    // ... rest of your methods
}