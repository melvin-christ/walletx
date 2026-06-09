package com.melvin.walletx.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponse {
    private String walletId;
    private String ownerName;
    private String ownerEmail;
    private BigDecimal balance;
    private String status;
    private String createdAt;
}