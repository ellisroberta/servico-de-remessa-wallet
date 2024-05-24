package com.example.servicoderemessawallet.messaging;

import com.example.servicoderemessawallet.dto.TransactionDTO;
import com.example.servicoderemessawallet.enums.TransactionStatusEnum;
import com.example.servicoderemessawallet.model.Transaction;
import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.repository.TransactionRepository;
import com.example.servicoderemessawallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class WalletEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(WalletEventConsumer.class);

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public WalletEventConsumer(TransactionRepository transactionRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue}")
    public void processWalletEvent(TransactionDTO transactionDTO) {
        try {
            logger.info("Transação recebida: {}", transactionDTO);

            if (isTransactionProcessed(transactionDTO.getWalletId())) {
                logger.warn("Transação já processada anteriormente. Ignorando...");
                return;
            }

            Wallet fromWallet = getWalletByUserId(transactionDTO.getFromUserId());
            validateSufficientBalance(fromWallet, transactionDTO.getAmountBrl());
            updateWalletBalance(fromWallet, transactionDTO.getAmountBrl());

            saveTransaction(transactionDTO);
            logger.info("Transação processada com sucesso. Wallet de origem atualizada: {}", fromWallet);

        } catch (Exception e) {
            logger.error("Erro ao processar mensagem do RabbitMQ: {}", transactionDTO, e);
        }
    }

    private boolean isTransactionProcessed(UUID transactionId) {
        return transactionRepository.existsById(transactionId);
    }

    private Wallet getWalletByUserId(UUID userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet não encontrada para o usuário de origem da transação."));
    }

    private void validateSufficientBalance(Wallet wallet, BigDecimal transactionAmount) {
        if (wallet.getBalanceBrl().compareTo(transactionAmount) < 0) {
            throw new RuntimeException("Saldo insuficiente na Wallet do usuário de origem.");
        }
    }

    private void updateWalletBalance(Wallet wallet, BigDecimal transactionAmount) {
        wallet.setBalanceBrl(wallet.getBalanceBrl().subtract(transactionAmount));
        walletRepository.save(wallet);
    }

    private void saveTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setWalletId(transactionDTO.getWalletId());
        transaction.setFromUserId(transactionDTO.getFromUserId());
        transaction.setToUserId(transactionDTO.getToUserId());
        transaction.setAmountBrl(transactionDTO.getAmountBrl());
        transaction.setExchangeRate(transactionDTO.getExchangeRate());
        transaction.setAmountUsd(transactionDTO.getAmountUsd());
        transaction.setDate(transactionDTO.getDate());
        transaction.setStatus(transactionDTO.getStatus() != null ? transactionDTO.getStatus() : TransactionStatusEnum.PENDING);
        transactionRepository.save(transaction);
    }
}
