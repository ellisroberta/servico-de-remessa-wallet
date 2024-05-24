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

    private final TransactionService transactionService;
    private final WalletService walletService;
    private final ExchangeRateService exchangeRateService;

    public TransactionListener(TransactionService transactionService,
                               WalletService walletService,
                               ExchangeRateService exchangeRateService){
        this.transactionService = transactionService;
        this.walletService = walletService;
        this.exchangeRateService = exchangeRateService;
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
