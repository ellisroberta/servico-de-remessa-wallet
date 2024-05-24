package com.example.servicoderemessawallet.repository;

import com.example.servicoderemessawallet.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, LocalDate> {
}
