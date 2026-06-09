package com.melvin.walletx.service;

import com.melvin.walletx.dto.TransactionRequest;
import com.melvin.walletx.dto.WalletResponse;
import com.melvin.walletx.entity.Transaction;
import com.melvin.walletx.entity.Wallet;
import com.melvin.walletx.exception.WalletException;
import com.melvin.walletx.repository.TransactionRepository;
import com.melvin.walletx.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Cacheable(value = "walletBalance", key = "#walletId")
    public WalletResponse getBalance(String walletId) {
        log.info("Cache MISS — fetching balance from DB for {}", walletId);
        Wallet wallet = findWallet(walletId);
        return toResponse(wallet);
    }

    @Transactional
    @CacheEvict(value = "walletBalance", key = "#request.walletId")
    @Retryable(retryFor = Exception.class, maxAttempts = 3,
            backoff = @Backoff(delay = 500))
    public WalletResponse credit(TransactionRequest request) {
        Wallet wallet = findWallet(request.getWalletId());
        assertWalletActive(wallet);

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);
        recordTransaction(wallet, Transaction.TransactionType.CREDIT,
                request.getAmount(), request.getDescription());

        log.info("Credited {} to wallet {}", request.getAmount(), wallet.getWalletId());
        return toResponse(wallet);
    }

    @Transactional
    @CacheEvict(value = "walletBalance", key = "#request.walletId")
    @Retryable(retryFor = Exception.class, maxAttempts = 3,
            backoff = @Backoff(delay = 500))
    public WalletResponse debit(TransactionRequest request) {
        Wallet wallet = findWallet(request.getWalletId());
        assertWalletActive(wallet);

        if (wallet.getBalance().compareTo(request.getAmount()) < 0)
            throw WalletException.badRequest("Insufficient balance");

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);
        recordTransaction(wallet, Transaction.TransactionType.DEBIT,
                request.getAmount(), request.getDescription());

        log.info("Debited {} from wallet {}", request.getAmount(), wallet.getWalletId());
        return toResponse(wallet);
    }

    public Page<Transaction> getTransactionHistory(String walletId, Pageable pageable) {
        Wallet wallet = findWallet(walletId);
        return transactionRepository.findByWalletOrderByCreatedAtDesc(wallet, pageable);
    }

    private Wallet findWallet(String walletId) {
        return walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> WalletException.notFound("Wallet not found: " + walletId));
    }

    private void assertWalletActive(Wallet wallet) {
        if (wallet.getStatus() != Wallet.WalletStatus.ACTIVE)
            throw WalletException.badRequest("Wallet is not active");
    }

    private void recordTransaction(Wallet wallet, Transaction.TransactionType type,
                                   BigDecimal amount, String description) {
        Transaction txn = Transaction.builder()
                .referenceId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .wallet(wallet)
                .type(type)
                .amount(amount)
                .balanceAfter(wallet.getBalance())
                .description(description)
                .build();
        transactionRepository.save(txn);
    }

    private WalletResponse toResponse(Wallet wallet) {
        return WalletResponse.builder()
                .walletId(wallet.getWalletId())
                .ownerName(wallet.getUser().getFullName())
                .ownerEmail(wallet.getUser().getEmail())
                .balance(wallet.getBalance())
                .status(wallet.getStatus().name())
                .createdAt(wallet.getCreatedAt().toString())
                .build();
    }
}