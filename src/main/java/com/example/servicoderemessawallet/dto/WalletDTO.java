package com.example.servicoderemessawallet.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WalletDTO {
    private UUID userId;
    private BigDecimal balanceBrl;
    private BigDecimal balanceUsd;
}
