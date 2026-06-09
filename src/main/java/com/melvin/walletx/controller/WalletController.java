package com.melvin.walletx.controller;

import com.melvin.walletx.dto.ApiResponse;
import com.melvin.walletx.dto.TransactionRequest;
import com.melvin.walletx.dto.WalletResponse;
import com.melvin.walletx.entity.Transaction;
import com.melvin.walletx.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{walletId}/balance")
    public ResponseEntity<ApiResponse<WalletResponse>> getBalance(
            @PathVariable String walletId) {
        return ResponseEntity.ok(
                ApiResponse.ok("Balance fetched", walletService.getBalance(walletId)));
    }

    @PostMapping("/credit")
    public ResponseEntity<ApiResponse<WalletResponse>> credit(
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Credit successful", walletService.credit(request)));
    }

    @PostMapping("/debit")
    public ResponseEntity<ApiResponse<WalletResponse>> debit(
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Debit successful", walletService.debit(request)));
    }

    @GetMapping("/{walletId}/transactions")
    public ResponseEntity<ApiResponse<Page<Transaction>>> getTransactions(
            @PathVariable String walletId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Transaction> txns = walletService.getTransactionHistory(
                walletId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.ok("Transactions fetched", txns));
    }
}