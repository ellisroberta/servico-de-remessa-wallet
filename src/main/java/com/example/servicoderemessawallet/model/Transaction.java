package com.example.servicoderemessawallet.model;

import com.example.servicoderemessawallet.enums.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Transaction implements Serializable {
    @Serial
    private static final long serialVersionUID = -8200813275894988737L;

    public Transaction(UUID walletId, UUID fromUserId, UUID toUserId, BigDecimal amountBrl, BigDecimal amountUsd,
                       TransactionTypeEnum transactionType) {
        this.walletId = walletId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amountBrl = amountBrl;
        this.amountUsd = amountUsd;
        this.transactionType = transactionType;
        this.date = LocalDateTime.now(); // Define a data/hora da transação para o momento atual
    }

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
    private BigDecimal exchangeRate;

    private LocalDateTime date;
    private TransactionTypeEnum transactionType;

    @PrePersist
    protected void onCreate() {
        date = LocalDateTime.now();
    }
}
