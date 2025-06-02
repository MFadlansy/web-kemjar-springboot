package com.example.healthapp.controller;

import com.example.healthapp.model.User;
import com.example.healthapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            if (user == null) {
                return ResponseEntity.status(404).body("Pengguna tidak ditemukan.");
            }
            // Untuk tujuan pembelajaran, tampilkan AES key yang didekripsi
            String decryptedAesKey = userService.getDecryptedAesKeyForUser(user);
            return ResponseEntity.ok(new com.example.healthapp.dto.UserProfileResponse(user.getUsername(), decryptedAesKey));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saat mengambil profil: " + e.getMessage());
        }
    }
}