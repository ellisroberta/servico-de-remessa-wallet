package com.example.servicoderemessawallet.model;

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
    private static final long serialVersionUID = 1733082919857121595L;

    @Id
    @GeneratedValue
    private UUID id;

//    @ManyToOne
//    @JoinColumn(name = "from_user_id", nullable = false)
//    private User fromUser;
//
//    @ManyToOne
//    @JoinColumn(name = "to_user_id", nullable = false)
//    private User toUser;

    private BigDecimal amountBrl;
    private BigDecimal amountUsd;
    private BigDecimal exchangeRate;

    private LocalDateTime date;

    @PrePersist
    protected void onCreate() {
        date = LocalDateTime.now();
    }
}
