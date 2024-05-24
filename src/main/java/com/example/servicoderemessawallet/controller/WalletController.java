package com.example.servicoderemessawallet.controller;

import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.repository.WalletRepository;
import com.example.servicoderemessawallet.service.WalletService;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
@Api(tags = "Carteiras")
public class WalletController {

    private final WalletRepository walletRepository;

    public WalletController(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Wallet> getWalletByUserId(@PathVariable UUID userId) {
        return walletRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody Wallet wallet) {
        Wallet newWallet = walletRepository.save(wallet);
        return ResponseEntity.status(HttpStatus.CREATED).body(newWallet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Wallet> updateWallet(@PathVariable UUID id, @RequestBody Wallet updatedWallet) {
        return walletRepository.findById(id)
                .map(existingWallet -> {
                    existingWallet.setBalanceBrl(updatedWallet.getBalanceBrl());
                    existingWallet.setBalanceUsd(updatedWallet.getBalanceUsd());
                    Wallet savedWallet = walletRepository.save(existingWallet);
                    return ResponseEntity.ok().body(savedWallet);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(@PathVariable UUID id) {
        // Deleta uma Wallet pelo ID
        walletRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
