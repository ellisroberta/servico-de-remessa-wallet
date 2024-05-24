package com.example.servicoderemessawallet.service;

import com.example.servicoderemessawallet.enums.TransactionStatusEnum;
import com.example.servicoderemessawallet.exception.InsufficientBalanceException;
import com.example.servicoderemessawallet.exception.WalletNotFoundException;
import com.example.servicoderemessawallet.model.ExchangeRate;
import com.example.servicoderemessawallet.model.Transaction;
import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.repository.TransactionRepository;
import com.example.servicoderemessawallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final ExchangeRateService exchangeRateService;

    public TransactionService(WalletRepository walletRepository,
                              TransactionRepository transactionRepository,
                              ExchangeRateService exchangeRateService) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.exchangeRateService = exchangeRateService;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada: " + transactionId));
    }

    @Transactional
    public Transaction createTransaction(UUID fromUserId, UUID toUserId, BigDecimal amountBrl) {
        Wallet fromWallet = walletRepository.findByUserId(fromUserId)
                .orElseThrow(() -> new WalletNotFoundException("Carteira não encontrada para o usuário: " + fromUserId));

        Wallet toWallet = walletRepository.findByUserId(toUserId)
                .orElseThrow(() -> new WalletNotFoundException("Carteira não encontrada para o usuário: " + toUserId));

        if (fromWallet.getBalanceBrl().compareTo(amountBrl) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente na carteira de origem");
        }

        BigDecimal exchangeRate = exchangeRateService.getDollarExchangeRate();
        BigDecimal amountUsd = amountBrl.divide(exchangeRate, 2, RoundingMode.HALF_UP);

        fromWallet.setBalanceBrl(fromWallet.getBalanceBrl().subtract(amountBrl));
        toWallet.setBalanceUsd(toWallet.getBalanceUsd().add(amountUsd));

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        Transaction transaction = Transaction.builder()
                .walletId(fromWallet.getId())
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .amountBrl(amountBrl)
                .amountUsd(amountUsd)
                .exchangeRate(exchangeRate)
                .status(TransactionStatusEnum.PENDING)
                .build();

        return transactionRepository.save(transaction);
    }
}
