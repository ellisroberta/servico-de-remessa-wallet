package com.example.servicoderemessawallet.repository;

import com.example.servicoderemessawallet.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT COALESCE(SUM(t.amountBrl), 0) FROM Transaction t WHERE t.fromUserId = :userId AND t.date >= :startDate AND t.date <= :endDate")
    BigDecimal sumTransactionsByUserAndDate(@Param("userId") UUID userId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}
