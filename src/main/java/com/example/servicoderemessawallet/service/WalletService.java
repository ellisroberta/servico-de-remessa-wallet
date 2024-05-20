package com.example.servicoderemessawallet.service;

import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    public List<Wallet> findAll() {
        return walletRepository.findAll();
    }

    public Wallet findById(UUID id) {
        return walletRepository.findById(id).orElse(null);
    }

    public Wallet save(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    public void deleteById(UUID id) {
        walletRepository.deleteById(id);
    }

    @Transactional
    public Wallet createWallet(UUID userId) {
        // Verifica se já existe uma carteira para o usuário
        Wallet existingWallet = walletRepository.findByUserId(userId);
        if (existingWallet != null) {
            throw new IllegalArgumentException("User already has a wallet");
        }

        // Cria uma nova carteira
        Wallet wallet = new Wallet();

        // Define saldos iniciais opcionais
        wallet.setBalanceBrl(BigDecimal.ZERO);
        wallet.setBalanceUsd(BigDecimal.ZERO);

        // Salva a carteira no banco de dados
        return walletRepository.save(wallet);
    }

    /**
     * Encontra a carteira associada ao usuário pelo seu ID.
     *
     * @param userId ID do usuário
     * @return Carteira associada ao usuário, se existir
     */
    public Wallet findWalletByUserId(UUID userId) {
        return walletRepository.findByUserId(userId);
    }

    @Transactional
    public void transfer(UUID senderId, UUID receiverId, BigDecimal amount) {
        Wallet senderWallet = walletRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        Wallet receiverWallet = walletRepository.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Verifica se o saldo do remetente é suficiente
        if (senderWallet.getBalanceBrl().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Realiza a transferência
        senderWallet.setBalanceBrl(senderWallet.getBalanceBrl().subtract(amount));
        receiverWallet.setBalanceBrl(receiverWallet.getBalanceBrl().add(amount));

        // Salva as carteiras atualizadas
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);
    }

}
