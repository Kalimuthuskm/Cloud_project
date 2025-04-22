// src/main/java/com/cloudstorage/model/BlockchainLedger.java

package com.cloudstorage.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockchainLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String action;         // upload/delete
    private String performedBy;    // username/admin
    private String hash;           // simulate file hash
    private LocalDateTime timestamp;
}
