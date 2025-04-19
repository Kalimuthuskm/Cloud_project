package com.cloudstorage.repository;

import com.cloudstorage.model.FileBackup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileBackupRepository extends JpaRepository<FileBackup, Long> {
}