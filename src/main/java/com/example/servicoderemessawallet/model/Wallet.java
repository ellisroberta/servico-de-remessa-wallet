package com.example.servicoderemessawallet.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Wallet implements Serializable {

    @Serial
    private static final long serialVersionUID = -704234683282881237L;

    @Id
    @GeneratedValue
    private UUID id;

    private BigDecimal balanceBrl;
    private BigDecimal balanceUsd;

//    @OneToOne(mappedBy = "wallet")
//    private User user;
}
