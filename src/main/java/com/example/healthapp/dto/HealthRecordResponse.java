package com.example.healthapp.dto;

import com.example.healthapp.model.HealthRecord;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class HealthRecordResponse {
    private Long id;
    private LocalDate recordDate;
    private String recordType;
    private String details; // Setelah didekripsi
    private LocalDateTime createdAt;

    public HealthRecordResponse(HealthRecord record, String decryptedDetails) {
        this.id = record.getId();
        this.recordDate = record.getRecordDate();
        this.recordType = record.getRecordType();
        this.details = decryptedDetails;
        this.createdAt = record.getCreatedAt();
    }

    // Getters
    public Long getId() { return id; }
    public LocalDate getRecordDate() { return recordDate; }
    public String getRecordType() { return recordType; }
    public String getDetails() { return details; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}