package com.example.servicoderemessawallet.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
public class ExchangeRate {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "rate")
    private BigDecimal rate;
}
