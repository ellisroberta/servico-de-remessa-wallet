package com.example.servicoderemessawallet.messaging;

import com.example.servicoderemessawallet.model.Transaction;
import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.repository.TransactionRepository;
import com.example.servicoderemessawallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

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
    public void processWalletEvent(Transaction transaction) {
        try {
            logger.info("Transação recebida: {}", transaction);

            // Verifica se a transação já foi processada anteriormente
            if (transactionRepository.existsById(transaction.getId())) {
                logger.warn("Transação já processada anteriormente. Ignorando...");
                return;
            }

            // Busca a Wallet do usuário de origem (fromUserId)
            Optional<Wallet> optionalFromWallet = walletRepository.findByUserId(transaction.getFromUserId());
            Wallet fromWallet = optionalFromWallet.orElseThrow(() ->
                    new RuntimeException("Wallet não encontrada para o usuário de origem da transação."));

            // Verifica se há saldo suficiente para realizar a transação
            BigDecimal transactionAmount = transaction.getAmountBrl();
            BigDecimal currentBalance = fromWallet.getBalanceBrl();
            if (currentBalance.compareTo(transactionAmount) < 0) {
                throw new RuntimeException("Saldo insuficiente na Wallet do usuário de origem.");
            }

            // Atualiza o saldo da Wallet de origem (subtrai o valor da transação)
            fromWallet.setBalanceBrl(currentBalance.subtract(transactionAmount));

            // Salva a Wallet de origem atualizada
            walletRepository.save(fromWallet);

            // Marca a transação como processada
            transactionRepository.save(transaction);

            logger.info("Transação processada com sucesso. Wallet de origem atualizada: {}", fromWallet);

        } catch (Exception e) {
            logger.error("Erro ao processar mensagem do RabbitMQ: {}", transaction, e);
        }
    }
}
