package com.example.servicoderemessawallet.repository;

import com.example.servicoderemessawallet.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByFromUserIdAndDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);

    default BigDecimal sumTransactionsByUserAndDate(UUID userId, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = findByFromUserIdAndDateBetween(userId, startDate, endDate);
        return transactions.stream()
                .map(Transaction::getAmountBrl)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
