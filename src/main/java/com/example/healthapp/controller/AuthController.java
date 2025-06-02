package com.example.healthapp.controller;

import com.example.healthapp.dto.AuthRequest;
import com.example.healthapp.dto.AuthResponse;
import com.example.healthapp.model.User;
import com.example.healthapp.security.JwtUtil;
import com.example.healthapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest authRequest) {
        try {
            userService.registerUser(authRequest.getUsername(), authRequest.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse("Registrasi berhasil", null, null, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new AuthResponse(e.getMessage(), null, null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse("Registrasi gagal: " + e.getMessage(), null, null, null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            User user = userService.findByUsername(authRequest.getUsername());
            String token = jwtUtil.generateToken(user.getUsername());
            String decryptedAesKey = userService.getDecryptedAesKeyForUser(user); // Ambil AES key yang didekripsi

            return ResponseEntity.ok(new AuthResponse("Login berhasil", user.getId(), token, decryptedAesKey));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Username atau password salah", null, null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse("Login gagal: " + e.getMessage(), null, null, null));
        }
    }
}