package com.example.healthapp.dto;

public class UserProfileResponse {
    private String username;
    private String aesKey; // Kode AES yang didekripsi untuk user

    public UserProfileResponse(String username, String aesKey) {
        this.username = username;
        this.aesKey = aesKey;
    }

    // Getters
    public String getUsername() { return username; }
    public String getAesKey() { return aesKey; }
}