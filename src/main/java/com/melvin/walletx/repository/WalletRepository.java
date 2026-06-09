package com.melvin.walletx.repository;

import com.melvin.walletx.entity.Wallet;
import com.melvin.walletx.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByWalletId(String walletId);
    Optional<Wallet> findByUser(User user);
}