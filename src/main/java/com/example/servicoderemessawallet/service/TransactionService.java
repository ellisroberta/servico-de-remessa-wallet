package com.example.servicoderemessawallet.service;

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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransactionService {

//    @Autowired
//    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public Transaction createTransaction(UUID fromUserId, UUID toUserId,
                                         BigDecimal amountBrl, BigDecimal exchangeRate) {
//        User fromUser = userRepository.findById(fromUserId)
//                .orElseThrow(() -> new UserNotFoundException("Usuário remetente não encontrado."));
//        User toUser = userRepository.findById(toUserId)
//                .orElseThrow(() -> new UserNotFoundException("Usuário destinatário não encontrado."));
//
//        Wallet fromWallet = fromUser.getWallet();
//        Wallet toWallet = toUser.getWallet();
//
//        validateBalance(fromWallet, amountBrl);
//        validateDailyLimit(fromUser, amountBrl);
//
//        // Calcular o valor em USD
//        BigDecimal amountUsd = amountBrl.divide(exchangeRate, 2, RoundingMode.HALF_UP);
//
//        // Atualizar saldos
//        fromWallet.setBalanceBrl(fromWallet.getBalanceBrl().subtract(amountBrl));
//        toWallet.setBalanceUsd(toWallet.getBalanceUsd().add(amountUsd));
//
//        walletRepository.save(fromWallet);
//        walletRepository.save(toWallet);
//
//        // Criar transação
//        Transaction transaction = new Transaction();
//        transaction.setFromUser(fromUser);
//        transaction.setToUser(toUser);
//        transaction.setAmountBrl(amountBrl);
//        transaction.setAmountUsd(amountUsd);
//        transaction.setExchangeRate(exchangeRate);
//        transaction.setDate(LocalDateTime.now());
//
//        return transactionRepository.save(transaction);
        return null;
    }

    private void validateBalance(Wallet fromWallet, BigDecimal amountBrl) {
        if (fromWallet.getBalanceBrl().compareTo(amountBrl) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente para realizar a remessa.");
        }
    }

//    private void validateDailyLimit(/*User user*/, BigDecimal amountBrl) {
//        BigDecimal dailyLimit = BigDecimal.valueOf(user.getUserType() == UserTypeEnum.PF ? 10000.00 : 50000.00);
//        BigDecimal transactionsToday = transactionRepository.sumTransactionsByUserAndDate(user.getId(), LocalDate.now());
//
//        BigDecimal totalTransactions = transactionsToday.add(amountBrl);
//
//        if (totalTransactions.compareTo(dailyLimit) > 0) {
//            throw new InsufficientBalanceException("Limite diário de transações excedido.");
//        }
//    }
}
