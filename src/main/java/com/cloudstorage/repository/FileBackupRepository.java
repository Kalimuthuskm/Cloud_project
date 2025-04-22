package com.cloudstorage.repository;

import com.cloudstorage.model.FileBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FileBackupRepository extends JpaRepository<FileBackup, Long> {
    void deleteByFilename(String filename);
Optional<FileBackup> findByFileName(String filename);



}