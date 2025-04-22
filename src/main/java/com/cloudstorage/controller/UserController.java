package com.cloudstorage.controller;

import com.cloudstorage.dto.UserProfileUpdateRequest;
import com.cloudstorage.model.User;
import com.cloudstorage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserProfileUpdateRequest request, Authentication auth) {
        String currentUsername = auth.getName();
        User user = userRepo.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid old password");
        }

        // Update username and password
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);

        return ResponseEntity.ok("✅ Profile updated successfully!");
    }
}
