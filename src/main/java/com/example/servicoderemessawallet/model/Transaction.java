package com.example.servicoderemessawallet.model;

import com.example.servicoderemessawallet.enums.TransactionStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_transaction")
public class Transaction implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID walletId;

    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    private UUID fromUserId;

    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    private UUID toUserId;

    private BigDecimal amountBrl;
    private BigDecimal amountUsd;
    @Embedded
    private ExchangeRate exchangeRate;
    @Enumerated(EnumType.STRING)
    private TransactionStatusEnum status;
}
