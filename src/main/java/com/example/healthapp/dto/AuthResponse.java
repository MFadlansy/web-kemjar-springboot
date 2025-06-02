package com.example.healthapp.dto;

public class AuthResponse {
    private String message;
    private Long userId;
    private String token; // JWT
    private String decryptedAesKey; // Untuk pembelajaran, dikirim ke frontend

    public AuthResponse(String message, Long userId, String token, String decryptedAesKey) {
        this.message = message;
        this.userId = userId;
        this.token = token;
        this.decryptedAesKey = decryptedAesKey;
    }

    // Getters
    public String getMessage() { return message; }
    public Long getUserId() { return userId; }
    public String getToken() { return token; }
    public String getDecryptedAesKey() { return decryptedAesKey; }
}