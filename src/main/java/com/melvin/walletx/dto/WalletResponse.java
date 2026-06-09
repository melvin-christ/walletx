package com.melvin.walletx.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponse implements Serializable {
    private String walletId;
    private String ownerName;
    private String ownerEmail;
    private BigDecimal balance;
    private String status;
    private String createdAt;
}