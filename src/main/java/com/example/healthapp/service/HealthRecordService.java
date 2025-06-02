package com.example.healthapp.service;

import com.example.healthapp.model.HealthRecord;
import com.example.healthapp.model.User;
import com.example.healthapp.repository.HealthRecordRepository;
import com.example.healthapp.util.CryptoUtil;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.List;

@Service
public class HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final CryptoUtil cryptoUtil;

    public HealthRecordService(HealthRecordRepository healthRecordRepository, CryptoUtil cryptoUtil) {
        this.healthRecordRepository = healthRecordRepository;
        this.cryptoUtil = cryptoUtil;
    }

    public HealthRecord saveHealthRecord(User user, LocalDate recordDate, String recordType, String rawDetails, String aesKeyHex) throws Exception {
        SecretKey aesKey = cryptoUtil.convertHexToSecretKey(aesKeyHex);
        String encryptedDetails = cryptoUtil.encryptAES(rawDetails, aesKey);
        HealthRecord record = new HealthRecord(user, recordDate, recordType, encryptedDetails);
        return healthRecordRepository.save(record);
    }

    public List<HealthRecord> getHealthRecordsByUserId(Long userId) {
        return healthRecordRepository.findByUserIdOrderByRecordDateDesc(userId);
    }

    public String decryptRecordDetails(String encryptedDetails, String aesKeyHex) throws Exception {
        SecretKey aesKey = cryptoUtil.convertHexToSecretKey(aesKeyHex);
        return cryptoUtil.decryptAES(encryptedDetails, aesKey);
    }
}