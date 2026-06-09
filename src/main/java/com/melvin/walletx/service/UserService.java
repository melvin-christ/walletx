package com.melvin.walletx.service;

import com.melvin.walletx.dto.RegisterRequest;
import com.melvin.walletx.entity.User;
import com.melvin.walletx.entity.Wallet;
import com.melvin.walletx.exception.WalletException;
import com.melvin.walletx.repository.UserRepository;
import com.melvin.walletx.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw WalletException.conflict("Email already registered");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw WalletException.conflict("Phone already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword()) // We'll hash this in Phase 3
                .phone(request.getPhone())
                .build();

        user = userRepository.save(user);
        log.info("User registered: {}", user.getEmail());

        // Auto-create wallet on registration
        Wallet wallet = Wallet.builder()
                .walletId("WLT-" + String.format("%05d", user.getId()))
                .user(user)
                .build();

        walletRepository.save(wallet);
        log.info("Wallet created: {} for user: {}", wallet.getWalletId(), user.getEmail());

        return user;
    }
}