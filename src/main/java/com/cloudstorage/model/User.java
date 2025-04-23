package com.cloudstorage.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Optional;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id @GeneratedValue
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role; // values: USER, ADMIN
 
    // getters, setters
    public Optional<User> findByUsername(String username) {
        // Implement the logic to find a user by username
        return Optional.empty(); // Placeholder implementation
    }

}
