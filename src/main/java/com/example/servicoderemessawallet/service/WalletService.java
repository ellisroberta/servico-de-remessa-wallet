package com.example.servicoderemessawallet.service;

//import com.example.servicoderemessawallet.model.User;
import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Transactional
    public Wallet createWallet(UUID userId) {
        // Verifica se já existe uma carteira para o usuário
        Wallet existingWallet = walletRepository.findByUserId(userId);
        if (existingWallet != null) {
            throw new IllegalArgumentException("User already has a wallet");
        }

        // Obter o usuário correspondente ao userId (exemplo hipotético)
//        User user = getUserById(userId);
//
        Wallet wallet = new Wallet();
//        wallet.setUser(user);

        // Define saldos iniciais opcionais
        wallet.setBalanceBrl(BigDecimal.ZERO);
        wallet.setBalanceUsd(BigDecimal.ZERO);

        // Salva a carteira no banco de dados
        return walletRepository.save(wallet);
    }

    public Wallet findWalletByUserId(UUID userId) {
        return walletRepository.findByUserId(userId);
    }

    // Método hipotético para obter um usuário por ID (simulação)
//    private User getUserById(UUID userId) {
//        // Lógica para buscar o usuário no banco de dados ou serviço externo
//        // Este método é hipotético e deve ser substituído pela sua lógica real
//        return new User(); // Exemplo simples de retorno de um usuário vazio
//    }
}
