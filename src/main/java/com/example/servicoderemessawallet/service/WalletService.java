package com.example.servicoderemessawallet.service;

import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Optional<Wallet> getWalletByUserId(UUID userId) {
        return walletRepository.findByUserId(userId);
    }

    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    public Wallet createWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    public Optional<Wallet> updateWallet(UUID id, Wallet updatedWallet) {
        return walletRepository.findById(id)
                .map(existingWallet -> {
                    existingWallet.setBalanceBrl(updatedWallet.getBalanceBrl());
                    existingWallet.setBalanceUsd(updatedWallet.getBalanceUsd());
                    return walletRepository.save(existingWallet);
                });
    }

    public void deleteWallet(UUID id) {
        walletRepository.deleteById(id);
    }

}
