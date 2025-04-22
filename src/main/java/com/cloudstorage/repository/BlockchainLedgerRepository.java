package com.cloudstorage.repository;

import com.cloudstorage.model.BlockchainLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockchainLedgerRepository extends JpaRepository<BlockchainLedger, Long> {
}
