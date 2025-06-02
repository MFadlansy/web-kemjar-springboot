package com.example.healthapp.service;

import java.security.PrivateKey;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.example.healthapp.model.HealthRecord;
import com.example.healthapp.model.User;
import com.example.healthapp.repository.HealthRecordRepository; // Tambahkan import ini
import com.example.healthapp.util.CryptoUtil; // Tambahkan import ini

@Service
public class HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final CryptoUtil cryptoUtil;
    private final UserService userService; // Tambahkan ini untuk akses UserService

    public HealthRecordService(HealthRecordRepository healthRecordRepository, CryptoUtil cryptoUtil, UserService userService) {
        this.healthRecordRepository = healthRecordRepository;
        this.cryptoUtil = cryptoUtil;
        this.userService = userService; // Inject UserService
    }

    // Metode saveHealthRecord yang diubah: sekarang mengenkripsi di backend
    public HealthRecord saveHealthRecord(User user, LocalDate recordDate, String recordType, String rawDetails) throws Exception {
        // 1. Dapatkan kunci AES pengguna yang didekripsi dari User
        // Asumsi private_rsa_key tersimpan di DB sebagai Base64
        byte[] privateKeyBytes = Base64.getDecoder().decode(user.getPrivateRsaKey());
        PrivateKey rsaPrivateKey = cryptoUtil.getPrivateKeyFromBytes(privateKeyBytes); // Gunakan metode dari CryptoUtil

        SecretKey decryptedAesKey = cryptoUtil.decryptAESKeyWithRSA(user.getAesKey(), rsaPrivateKey);

        // 2. Enkripsi detail mentah dengan kunci AES yang didekripsi
        String encryptedDetails = cryptoUtil.encryptAES(rawDetails, decryptedAesKey);

        HealthRecord record = new HealthRecord(user, recordDate, recordType, encryptedDetails);
        return healthRecordRepository.save(record);
    }

    public List<HealthRecord> getHealthRecordsByUserId(Long userId) {
        return healthRecordRepository.findByUserIdOrderByRecordDateDesc(userId);
    }

    // Metode dekripsi tetap di sini, digunakan saat membaca data
    public String decryptRecordDetails(String encryptedDetails, String aesKeyHex) throws Exception {
        SecretKey aesKey = cryptoUtil.convertHexToSecretKey(aesKeyHex);
        return cryptoUtil.decryptAES(encryptedDetails, aesKey);
    }
}