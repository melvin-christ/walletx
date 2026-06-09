package com.melvin.walletx.repository;

import com.melvin.walletx.entity.Transaction;
import com.melvin.walletx.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByWalletOrderByCreatedAtDesc(Wallet wallet, Pageable pageable);
}