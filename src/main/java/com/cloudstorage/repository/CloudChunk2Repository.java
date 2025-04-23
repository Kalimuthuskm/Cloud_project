package com.cloudstorage.repository;

import com.cloudstorage.model.CloudChunk2;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CloudChunk2Repository extends JpaRepository<CloudChunk2, Long> {
    Optional<CloudChunk2> findByFileName(String filename);

    void deleteByFilename(String filename);


}