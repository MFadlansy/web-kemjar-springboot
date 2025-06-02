package com.example.healthapp.controller;

import com.example.healthapp.dto.HealthRecordRequest;
import com.example.healthapp.dto.HealthRecordResponse;
import com.example.healthapp.model.HealthRecord;
import com.example.healthapp.model.User;
import com.example.healthapp.service.HealthRecordService;
import com.example.healthapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class HealthRecordController {

    private final HealthRecordService healthRecordService;
    private final UserService userService;

    public HealthRecordController(HealthRecordService healthRecordService, UserService userService) {
        this.healthRecordService = healthRecordService;
        this.userService = userService;
    }

    @PostMapping("/health-records")
    public ResponseEntity<?> addHealthRecord(@AuthenticationPrincipal UserDetails userDetails,
                                             @Valid @RequestBody HealthRecordRequest request,
                                             @RequestHeader("X-AES-Key") String aesKeyHex) { // Menerima AES key dari header
        try {
            User currentUser = userService.findByUsername(userDetails.getUsername());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pengguna tidak ditemukan.");
            }

            HealthRecord savedRecord = healthRecordService.saveHealthRecord(
                    currentUser, request.getRecordDate(), request.getRecordType(), request.getDetails(), aesKeyHex
            );
            return ResponseEntity.status(HttpStatus.CREATED).body("Catatan kesehatan berhasil ditambahkan.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal menambahkan catatan: " + e.getMessage());
        }
    }

    @GetMapping("/health-records")
    public ResponseEntity<?> getHealthRecords(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestHeader("X-AES-Key") String aesKeyHex) { // Menerima AES key dari header
        try {
            User currentUser = userService.findByUsername(userDetails.getUsername());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pengguna tidak ditemukan.");
            }

            List<HealthRecord> records = healthRecordService.getHealthRecordsByUserId(currentUser.getId());
            List<HealthRecordResponse> responses = records.stream().map(record -> {
                try {
                    String decryptedDetails = healthRecordService.decryptRecordDetails(record.getEncryptedDetails(), aesKeyHex);
                    return new HealthRecordResponse(record, decryptedDetails);
                } catch (Exception e) {
                    System.err.println("Gagal mendekripsi catatan ID " + record.getId() + ": " + e.getMessage());
                    return new HealthRecordResponse(record, "[Gagal Dekripsi Data]");
                }
            }).collect(Collectors.toList());

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal memuat catatan kesehatan: " + e.getMessage());
        }
    }
}