package com.example.servicoderemessawallet.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class ExchangeRate {
    @Id
    private LocalDate date;
    private BigDecimal rate;
}
