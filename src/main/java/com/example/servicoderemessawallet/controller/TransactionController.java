package com.example.servicoderemessawallet.controller;

import com.example.servicoderemessawallet.model.Transaction;
import com.example.servicoderemessawallet.service.ExchangeRateService;
import com.example.servicoderemessawallet.service.TransactionService;
import com.example.servicoderemessawallet.service.WalletService;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/servico-de-remessa/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @ApiOperation("Cria uma nova transação entre carteiras.")
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @RequestParam UUID fromUserId,
            @RequestParam UUID toUserId,
            @RequestParam @Valid BigDecimal amountBrl) {

        // Validação dos parâmetros de entrada
        if (fromUserId == null || toUserId == null || amountBrl.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Parâmetros inválidos para criar transação.");
        }

        // Obter taxa de câmbio do dólar
        BigDecimal exchangeRate = exchangeRateService.getDollarExchangeRate();

        // Criar a transação
        Transaction transaction = transactionService.createTransaction(fromUserId, toUserId, amountBrl, exchangeRate);

        // Retornar a transação criada com status 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }
}
