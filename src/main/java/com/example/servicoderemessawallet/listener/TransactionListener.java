package com.example.servicoderemessawallet.listener;

import com.example.servicoderemessawallet.service.ExchangeRateService;
import com.example.servicoderemessawallet.service.TransactionService;
import com.example.servicoderemessawallet.service.WalletService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class TransactionListener {

    private TransactionService transactionService;

    private WalletService walletService;
    private ExchangeRateService exchangeRateService;

    @RabbitListener(queues = "transactionQueue")
    public void handleTransactionMessage(String message) {
        // Parse the message (assuming it's a simple comma-separated format for this example)
        String[] parts = message.split(",");
        UUID fromUserId = UUID.fromString(parts[0]);
        UUID toUserId = UUID.fromString(parts[1]);
        BigDecimal amountBrl = new BigDecimal(parts[2]);

        // Criar a transação
        transactionService.createTransaction(fromUserId, toUserId, amountBrl);
    }
}
