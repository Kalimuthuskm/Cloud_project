package com.cloudstorage.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Backup {
    @Id
    private Long id;

    // Add other fields, getters, setters, and constructors as needed

    public Backup() {
    }

    public Backup(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Override equals and hashCode if necessary
}