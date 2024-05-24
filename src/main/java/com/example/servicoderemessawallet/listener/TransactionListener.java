package com.example.servicoderemessawallet.listener;

import com.example.servicoderemessawallet.service.TransactionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class TransactionListener {

    private final TransactionService transactionService;

    public TransactionListener(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @RabbitListener(queues = "transactionQueue")
    public void handleTransactionMessage(String message) {
        String[] parts = message.split(",");
        UUID fromUserId = UUID.fromString(parts[0]);
        UUID toUserId = UUID.fromString(parts[1]);
        BigDecimal amountBrl = new BigDecimal(parts[2]);

        transactionService.createTransaction(fromUserId, toUserId, amountBrl);
    }
}
