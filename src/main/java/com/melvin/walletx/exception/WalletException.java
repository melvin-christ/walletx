package com.melvin.walletx.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class WalletException extends RuntimeException {

    private final HttpStatus status;

    public WalletException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    // Common factory methods
    public static WalletException notFound(String message) {
        return new WalletException(message, HttpStatus.NOT_FOUND);
    }

    public static WalletException badRequest(String message) {
        return new WalletException(message, HttpStatus.BAD_REQUEST);
    }

    public static WalletException conflict(String message) {
        return new WalletException(message, HttpStatus.CONFLICT);
    }
}