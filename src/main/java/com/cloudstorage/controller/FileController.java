package com.cloudstorage.controller;

import com.cloudstorage.model.FileMetadata;
import com.cloudstorage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*") // ✅ Allow Postman & frontend
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
   
    @PostMapping("/upload")
    public ResponseEntity<FileMetadata> upload(@RequestParam("file") MultipartFile file) throws Exception {
    System.out.println("🔥 Uploading: " + (file != null ? file.getOriginalFilename() : "null"));
    return ResponseEntity.ok(fileService.uploadFile(file));
}
 
    @GetMapping
    public ResponseEntity<List<FileMetadata>> getAllFiles() {
        return ResponseEntity.ok(fileService.getAllFiles());
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable String filename) throws Exception {
        byte[] data = fileService.downloadFile(filename);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(data);
    }

    @GetMapping("/backup-download/{filename}")
    public ResponseEntity<byte[]> backupDownload(@PathVariable String filename) throws Exception {
        byte[] data = fileService.downloadFromBackup(filename);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(data);
    }

    @DeleteMapping("/delete/{filename}")
    public ResponseEntity<String> delete(@PathVariable String filename) {
        fileService.deleteFile(filename);
        return ResponseEntity.ok("File deleted: " + filename);
    }
}