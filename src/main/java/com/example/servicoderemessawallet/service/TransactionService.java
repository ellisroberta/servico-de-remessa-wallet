package com.example.servicoderemessawallet.service;

import com.example.servicoderemessawallet.dto.TransactionDTO;
import com.example.servicoderemessawallet.enums.TransactionStatusEnum;
import com.example.servicoderemessawallet.exception.InsufficientBalanceException;
import com.example.servicoderemessawallet.exception.WalletNotFoundException;
import com.example.servicoderemessawallet.model.Transaction;
import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.repository.TransactionRepository;
import com.example.servicoderemessawallet.repository.WalletRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    private final RabbitTemplate rabbitTemplate;

    public TransactionService(WalletRepository walletRepository,
                              TransactionRepository transactionRepository,
                              ExchangeRateService exchangeRateService,
                              RabbitTemplate rabbitTemplate) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.exchangeRateService = exchangeRateService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada: " + transactionId));
    }
    private Wallet getWalletByUserId(UUID userId) throws WalletNotFoundException {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Carteira não encontrada para o usuário: " + userId));
    }

    @Transactional
    public void createAndSendTransaction(UUID fromUserId, UUID toUserId, BigDecimal amountBrl) {
        Wallet fromWallet = getWalletByUserId(fromUserId);
        Wallet toWallet = getWalletByUserId(toUserId);

        if (fromWallet.getBalanceBrl().compareTo(amountBrl) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente na carteira de origem");
        }

        BigDecimal exchangeRate = exchangeRateService.getDollarExchangeRate();
        BigDecimal amountUsd = amountBrl.divide(exchangeRate, 2, RoundingMode.HALF_UP);

        fromWallet.setBalanceBrl(fromWallet.getBalanceBrl().subtract(amountBrl));
        toWallet.setBalanceUsd(toWallet.getBalanceUsd().add(amountUsd));

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setWalletId(fromWallet.getId());
        transactionDTO.setFromUserId(fromUserId);
        transactionDTO.setToUserId(toUserId);
        transactionDTO.setAmountBrl(amountBrl);
        transactionDTO.setAmountUsd(amountUsd);
        transactionDTO.setExchangeRate(exchangeRate);
        transactionDTO.setStatus(TransactionStatusEnum.PENDING);

        // Envio da transação para o microsserviço Wallet através de RabbitMQ
        rabbitTemplate.convertAndSend("exchange", "routingKey", transactionDTO);
    }

    @Transactional
    public void processTransaction(TransactionDTO transactionDTO) {
        Wallet fromWallet = getWalletByUserId(transactionDTO.getFromUserId());
        Wallet toWallet = getWalletByUserId(transactionDTO.getToUserId());

        fromWallet.setBalanceBrl(fromWallet.getBalanceBrl().subtract(transactionDTO.getAmountBrl()));
        toWallet.setBalanceUsd(toWallet.getBalanceUsd().add(transactionDTO.getAmountUsd()));

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        Transaction transaction = Transaction.builder()
                .walletId(transactionDTO.getWalletId())
                .fromUserId(transactionDTO.getFromUserId())
                .toUserId(transactionDTO.getToUserId())
                .amountBrl(transactionDTO.getAmountBrl())
                .amountUsd(transactionDTO.getAmountUsd())
                .exchangeRate(transactionDTO.getExchangeRate())
                .status(transactionDTO.getStatus())
                .build();

        transactionRepository.save(transaction);
    }
}
