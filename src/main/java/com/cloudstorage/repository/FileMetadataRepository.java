package com.cloudstorage.repository;

import com.cloudstorage.model.FileMetadata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByUploadedBy(String username);
    
}
