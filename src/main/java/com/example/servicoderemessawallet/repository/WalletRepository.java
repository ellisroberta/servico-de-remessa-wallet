package com.example.servicoderemessawallet.repository;

import com.example.servicoderemessawallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    Wallet findByUserId(UUID userId);
}
