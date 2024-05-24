package com.example.servicoderemessawallet.model;

import com.example.servicoderemessawallet.enums.TransactionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    @JoinColumn(name = "from_user_id", nullable = false)
    @Type(type = "uuid-char")
    private UUID fromUserId;

    @JoinColumn(name = "to_user_id", nullable = false)
    @Type(type = "uuid-char")
    private UUID toUserId;

    private BigDecimal amountBrl;
    private BigDecimal amountUsd;
    private BigDecimal exchangeRate;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private TransactionStatusEnum status;

    @PrePersist
    protected void onCreate() {
        date = LocalDate.now();
    }
}
