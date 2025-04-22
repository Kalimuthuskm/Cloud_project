package com.cloudstorage.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChunkMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chunkName;
    private String fileName;
    private long chunkSize;
    private boolean encrypted;
    private LocalDateTime timestamp;
    private String uploadedBy;
}
