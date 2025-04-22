package com.cloudstorage.repository;

import com.cloudstorage.model.FileMetadata;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByUploadedBy(String username);

    Optional<FileMetadata> findByFilenameAndUploadedBy(String filename, String username);

    // Removed findById(Long id) as it is already inherited from JpaRepository
    void deleteById(Long id);    

    
}
