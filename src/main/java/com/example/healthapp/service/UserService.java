package com.example.healthapp.service;

import com.example.healthapp.model.User;
import com.example.healthapp.repository.UserRepository;
import com.example.healthapp.util.CryptoUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.SecretKey;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CryptoUtil cryptoUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CryptoUtil cryptoUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cryptoUtil = cryptoUtil;
    }

    public User registerUser(String username, String rawPassword) throws Exception {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);

        // Generate RSA and AES keys
        KeyPair rsaKeyPair = cryptoUtil.generateRSAKeyPair();
        PublicKey rsaPublicKey = rsaKeyPair.getPublic();
        PrivateKey rsaPrivateKey = rsaKeyPair.getPrivate();

        SecretKey aesKey = cryptoUtil.generateAESKey();

        // Encrypt AES key with RSA public key
        String encryptedAesKey = cryptoUtil.encryptAESKeyWithRSA(aesKey, rsaPublicKey);

        // Store private RSA key as Base64 string
        String privateRsaKeyPem = Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded());


        User newUser = new User(username, hashedPassword, encryptedAesKey, privateRsaKeyPem);
        return userRepository.save(newUser);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // Mengambil kunci AES yang sudah didekripsi (digunakan setelah login)
    public String getDecryptedAesKeyForUser(User user) throws Exception {
        // Asumsi private_rsa_key tersimpan di DB sebagai Base64
        // Anda perlu mengimpornya kembali sebagai PrivateKey
        byte[] privateKeyBytes = Base64.getDecoder().decode(user.getPrivateRsaKey());
        PrivateKey rsaPrivateKey = cryptoUtil.getPrivateKeyFromBytes(privateKeyBytes); // Anda perlu menambahkan metode ini di CryptoUtil

        SecretKey decryptedAesKey = cryptoUtil.decryptAESKeyWithRSA(user.getAesKey(), rsaPrivateKey);
        return cryptoUtil.convertSecretKeyToHex(decryptedAesKey);
    }
}