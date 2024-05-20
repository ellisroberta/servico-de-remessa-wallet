package com.example.servicoderemessawallet.handler;

import com.example.servicoderemessawallet.enums.TransactionTypeEnum;
import com.example.servicoderemessawallet.exception.WalletNotFoundException;
import com.example.servicoderemessawallet.model.Transaction;
import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.service.NotificationService;
import com.example.servicoderemessawallet.service.TransactionService;
import com.example.servicoderemessawallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class WalletEventHandler {

    private final WalletService walletService;

    private final TransactionService transactionService;

    private final NotificationService notificationService;

    @Autowired
    public WalletEventHandler(WalletService walletService, UUID fromUserId, UUID toUserId, TransactionService transactionService,
                              NotificationService notificationService) {
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.notificationService = notificationService;
    }

    @EventListener
    public void handleWalletUpdatedEvent(WalletUpdatedEvent event) {
        UUID walletId = event.getWalletId();
        BigDecimal amountBrl = event.getAmountBrl();
        BigDecimal amountUsd = event.getAmountUsd();

        // Exemplo de lógica: atualizar o saldo da carteira
        Wallet wallet = walletService.findById(walletId);
        if (wallet != null) {
            // Atualiza o saldo da carteira Brl
            wallet.setBalanceBrl(wallet.getBalanceBrl().add(amountBrl));
            walletService.save(wallet);

            // Atualiza o saldo da carteira Usd
            wallet.setBalanceUsd(wallet.getBalanceUsd().add(amountUsd));
            walletService.save(wallet);

            // Exemplo de registro de transação (pode ser implementado em um serviço separado)
            Transaction transaction = new Transaction(walletId, amountBrl, amountUsd, TransactionTypeEnum.DEPOSIT);
            transactionService.save(transaction);

            // Exemplo de envio de notificação (pode ser implementado em um serviço separado)
            notificationService.sendNotification(wallet.getEmail(), "Saldo da carteira atualizado",
                    "Seu saldo foi atualizado. Novo saldo BRL: " + wallet.getBalanceBrl() + ", Novo saldo USD: " + wallet.getBalanceUsd());

            // Outras operações de negócio relacionadas à atualização da carteira
        } else {
            // Lógica para lidar com a carteira não encontrada, se necessário
            throw new WalletNotFoundException("Carteira não encontrada para o ID: " + walletId);
        }
    }
}

