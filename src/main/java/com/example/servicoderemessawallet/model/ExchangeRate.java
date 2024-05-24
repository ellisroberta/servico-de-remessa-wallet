package com.example.servicoderemessawallet.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class ExchangeRate {
    @Id
    private LocalDate date;
    private BigDecimal rate;
}
