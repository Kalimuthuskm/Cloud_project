package com.cloudstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cloudstorage.model.Backup;

@Repository
public interface BackupRepository extends JpaRepository<Backup, Long> {
    // No additional methods are needed for deleting files by filename
}
