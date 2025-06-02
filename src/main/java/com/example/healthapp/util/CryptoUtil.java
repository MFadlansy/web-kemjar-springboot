package com.example.healthapp.util;

import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
public class CryptoUtil {

    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String RSA_ALGORITHM = "RSA";
    private static final int AES_KEY_SIZE = 256; // bits
    private static final int RSA_KEY_SIZE = 2048; // bits

    // --- RSA Utility Methods ---

    public KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(RSA_KEY_SIZE, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    public String encryptAESKeyWithRSA(SecretKey aesKey, PublicKey rsaPublicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        byte[] encryptedBytes = cipher.doFinal(aesKey.getEncoded());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public SecretKey decryptAESKeyWithRSA(String encryptedAesKeyBase64, PrivateKey rsaPrivateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedAesKeyBase64));
        return new SecretKeySpec(decryptedBytes, 0, decryptedBytes.length, "AES");
    }

    // --- AES Utility Methods ---

    public SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE, new SecureRandom()); // 256-bit key
        return keyGen.generateKey();
    }

    // Convert SecretKey to Hex String (for storing in DB or sending to frontend)
    public String convertSecretKeyToHex(SecretKey secretKey) {
        return bytesToHex(secretKey.getEncoded());
    }

    // Convert Hex String to SecretKey
    public SecretKey convertHexToSecretKey(String hexKey) {
        if (hexKey == null || hexKey.length() != 64) { // 32 bytes = 64 hex chars
            throw new IllegalArgumentException("Invalid AES key hex string length.");
        }
        byte[] keyBytes = hexToBytes(hexKey);
        return new SecretKeySpec(keyBytes, "AES");
    }

    // Encrypt data with AES
    public String encryptAES(String data, SecretKey aesKey) throws Exception {
        byte[] iv = new byte[16]; // 16 bytes for AES CBC IV
        new SecureRandom().nextBytes(iv); // Generate a random IV
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));

        // Combine IV and encrypted data
        return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt data with AES
    public String decryptAES(String encryptedDataWithIv, SecretKey aesKey) throws Exception {
        String[] parts = encryptedDataWithIv.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid encrypted data format. Expected IV:EncryptedData");
        }
        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] encryptedBytes = Base64.getDecoder().decode(parts[1]);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, "UTF-8");
    }

    // Helper: Convert byte array to hex string
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Helper: Convert hex string to byte array
    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    public PrivateKey getPrivateKeyFromBytes(byte[] privateKeyBytes) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

}