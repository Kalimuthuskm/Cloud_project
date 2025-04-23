package com.cloudstorage.repository;

import com.cloudstorage.model.CloudChunk1;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudChunk1Repository extends JpaRepository<CloudChunk1, Long> {

    Optional<CloudChunk1> findByFileName(String filename);
    
    void deleteByFilename(String filename);  // Add this method for deletion
}
