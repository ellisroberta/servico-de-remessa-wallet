package com.example.servicoderemessawallet.listener;

import com.example.servicoderemessawallet.dto.TransactionDTO;
import com.example.servicoderemessawallet.service.TransactionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private final TransactionService transactionService;

    public TransactionListener(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @RabbitListener(queues = "transactionQueue")
    public void handleTransactionMessage(TransactionDTO transactionDTO) {
        transactionService.processTransaction(transactionDTO);
    }
}
