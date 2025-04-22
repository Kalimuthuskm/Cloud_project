package com.cloudstorage.repository;

import com.cloudstorage.model.ChunkMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChunkMetadataRepository extends JpaRepository<ChunkMetadata, Long> {
    
    // 🔍 Get metadata for a specific file
    List<ChunkMetadata> findByFileName(String fileName);

    // 🔍 Optional: Get chunks uploaded by a specific user
    List<ChunkMetadata> findByUploadedBy(String uploadedBy);

    void deleteByFileName(String fileName);

}
