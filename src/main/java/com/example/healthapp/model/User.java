package com.example.healthapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // Password hash

    @Lob // Untuk menyimpan teks panjang
    @Column(name = "aes_key", nullable = false, columnDefinition = "LONGTEXT") // Tambahkan columnDefinition
    private String aesKey; // AES key terenkripsi dengan public RSA key

    @Lob // Untuk menyimpan teks panjang
    @Column(name = "private_rsa_key", nullable = false, columnDefinition = "LONGTEXT") // Tambahkan columnDefinition
    private String privateRsaKey; // Private RSA key pengguna

    // Constructors
    public User() {}

    public User(String username, String password, String aesKey, String privateRsaKey) {
        this.username = username;
        this.password = password;
        this.aesKey = aesKey;
        this.privateRsaKey = privateRsaKey;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getAesKey() { return aesKey; }
    public void setAesKey(String aesKey) { this.aesKey = aesKey; }
    public String getPrivateRsaKey() { return privateRsaKey; }
    public void setPrivateRsaKey(String privateRsaKey) { this.privateRsaKey = privateRsaKey; }
}