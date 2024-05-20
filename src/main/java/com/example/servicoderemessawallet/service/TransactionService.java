package com.example.servicoderemessawallet.service;

import com.example.servicoderemessawallet.enums.UserTypeEnum;
import com.example.servicoderemessawallet.exception.InsufficientBalanceException;
//import com.example.servicoderemessawallet.exception.UserNotFoundException;
import com.example.servicoderemessawallet.model.Transaction;
//import com.example.servicoderemessawallet.model.User;
import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.repository.TransactionRepository;
//import com.example.servicoderemessawallet.repository.UserRepository;
import com.example.servicoderemessawallet.repository.WalletRepository;
//import com.example.servicoderemessawallet.utils.enums.UserTypeEnum;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public Transaction createTransaction(UUID fromUserId, UUID toUserId,
                                         BigDecimal amountBrl, BigDecimal exchangeRate) {
        Wallet fromWallet = walletRepository.findByUserId(fromUserId)
                .orElseThrow(() -> new IllegalArgumentException("Carteira do remetente não encontrada."));
        Wallet toWallet = walletRepository.findByUserId(toUserId)
                .orElseThrow(() -> new IllegalArgumentException("Carteira do destinatário não encontrada."));

        validateBalanceBrl(fromWallet, amountBrl);
        // validateDailyLimit(fromUserId, amountBrl); // Método hipotético, se for necessário

        // Calcular o valor em USD
        BigDecimal amountUsd = amountBrl.divide(exchangeRate, 2, RoundingMode.HALF_UP);

        // Atualizar saldos
        fromWallet.setBalanceBrl(fromWallet.getBalanceBrl().subtract(amountBrl));
        toWallet.setBalanceUsd(toWallet.getBalanceUsd().add(amountUsd));

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        // Criar transação
        Transaction transaction = new Transaction();
        transaction.setFromUserId(fromUserId);
        transaction.setToUserId(toUserId);
        transaction.setAmountBrl(amountBrl);
        transaction.setAmountUsd(amountUsd);
        transaction.setExchangeRate(exchangeRate);
        transaction.setDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    private void validateBalanceBrl(Wallet fromWallet, BigDecimal amountBrl) {
        if (fromWallet.getBalanceBrl().compareTo(amountBrl) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente para realizar a remessa.");
        }
    }

//    private void validateBalanceUsd(Wallet fromWallet, BigDecimal amountUsd) {
//        if (fromWallet.getBalanceUsd().compareTo(amountUsd) < 0) {
//            throw new InsufficientBalanceException("Saldo insuficiente para realizar a remessa.");
//        }
//    }

    private void validateDailyLimit(UUID userId, UserTypeEnum userType, BigDecimal amountBrl) {
        BigDecimal dailyLimit = BigDecimal.valueOf(userType == UserTypeEnum.PF ? 10000.00 : 50000.00);
        BigDecimal transactionsToday = transactionRepository.sumTransactionsByUserAndDate(userId, LocalDateTime.now());

        BigDecimal totalTransactions = transactionsToday.add(amountBrl);

        if (totalTransactions.compareTo(dailyLimit) > 0) {
            throw new InsufficientBalanceException("Limite diário de transações excedido.");
        }
    }

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
