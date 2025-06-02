package com.example.healthapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class HealthRecordRequest {
    @NotNull(message = "Tanggal catatan tidak boleh kosong")
    private LocalDate recordDate;

    @NotBlank(message = "Jenis catatan tidak boleh kosong")
    private String recordType;

    @NotBlank(message = "Detail catatan tidak boleh kosong")
    private String details; // Ini akan berisi data terenkripsi dari frontend

    // Getters and Setters
    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }
    public String getRecordType() { return recordType; }
    public void setRecordType(String recordType) { this.recordType = recordType; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}