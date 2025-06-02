package com.example.healthapp.model;

import jakarta.persistence.*; // Menggunakan jakarta.persistence
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_records")
public class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Relasi Many-to-One ke User

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "record_type", nullable = false, length = 50)
    private String recordType;

    @Lob // Untuk menyimpan teks panjang
    @Column(name = "encrypted_details", nullable = false)
    private String encryptedDetails; // Data sensitif terenkripsi

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist // Otomatis mengisi createdAt sebelum disimpan
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructors
    public HealthRecord() {}

    public HealthRecord(User user, LocalDate recordDate, String recordType, String encryptedDetails) {
        this.user = user;
        this.recordDate = recordDate;
        this.recordType = recordType;
        this.encryptedDetails = encryptedDetails;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }
    public String getRecordType() { return recordType; }
    public void setRecordType(String recordType) { this.recordType = recordType; }
    public String getEncryptedDetails() { return encryptedDetails; }
    public void setEncryptedDetails(String encryptedDetails) { this.encryptedDetails = encryptedDetails; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}