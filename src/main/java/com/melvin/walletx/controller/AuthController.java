package com.melvin.walletx.controller;

import com.melvin.walletx.dto.*;
import com.melvin.walletx.entity.User;
import com.melvin.walletx.entity.Wallet;
import com.melvin.walletx.exception.WalletException;
import com.melvin.walletx.repository.UserRepository;
import com.melvin.walletx.repository.WalletRepository;
import com.melvin.walletx.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            throw WalletException.badRequest("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> WalletException.notFound("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> WalletException.notFound("Wallet not found"));

        String token = jwtUtil.generateToken(user.getEmail());

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .walletId(wallet.getWalletId())
                .build();

        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }
}