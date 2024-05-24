package com.example.servicoderemessawallet.dto;

import com.example.servicoderemessawallet.enums.TransactionStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class TransactionDTO {

    private UUID walletId;
    private UUID fromUserId;
    private UUID toUserId;
    private BigDecimal amountBrl;
    private BigDecimal amountUsd;
    private BigDecimal exchangeRate;
    private LocalDate date;
    private TransactionStatusEnum status;
}
