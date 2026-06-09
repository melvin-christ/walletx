package com.melvin.walletx.service;

import com.melvin.walletx.dto.RegisterRequest;
import com.melvin.walletx.entity.User;
import com.melvin.walletx.entity.Wallet;
import com.melvin.walletx.exception.WalletException;
import com.melvin.walletx.repository.UserRepository;
import com.melvin.walletx.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw WalletException.conflict("Email already registered");
        if (userRepository.existsByPhone(request.getPhone()))
            throw WalletException.conflict("Phone already registered");

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // hashed!
                .phone(request.getPhone())
                .build();

        user = userRepository.save(user);

        Wallet wallet = Wallet.builder()
                .walletId("WLT-" + String.format("%05d", user.getId()))
                .user(user)
                .build();
        walletRepository.save(wallet);

        log.info("Registered user {} with wallet {}", user.getEmail(), wallet.getWalletId());
        return user;
    }
}