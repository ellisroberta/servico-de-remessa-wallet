package com.example.servicoderemessawallet.repository;

import com.example.servicoderemessawallet.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("SELECT COALESCE(SUM(t.amountBrl), 0) FROM Transaction t WHERE t.fromUser.id = :userId AND t.date = :date")
    BigDecimal sumTransactionsByUserAndDate(UUID userId, LocalDate date);
}
