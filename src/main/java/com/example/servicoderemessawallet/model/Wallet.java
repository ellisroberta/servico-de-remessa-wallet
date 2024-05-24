package com.example.servicoderemessawallet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_wallet")
public class Wallet implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "balance_brl", nullable = false)
    private BigDecimal balanceBrl;
    @Column(name = "balance_usd", nullable = false)
    private BigDecimal balanceUsd;
}
