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
    private String action; // e.g., "UPLOAD", "DELETE"
    private String performedBy;
    private LocalDateTime timestamp;
    private String hash;
}
