package com.example.servicoderemessawallet.event;

import com.example.servicoderemessawallet.dto.TransactionDTO;
import com.example.servicoderemessawallet.enums.TransactionStatusEnum;
import com.example.servicoderemessawallet.messaging.WalletEventConsumer;
import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.repository.TransactionRepository;
import com.example.servicoderemessawallet.repository.WalletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletEventConsumerTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletEventConsumer walletEventConsumer;

    @Test
    @DisplayName("Deve processar transação com sucesso")
    void testProcessWalletEventSuccess() {
        UUID transactionId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        UUID fromUserId = UUID.randomUUID();
        UUID toUserId = UUID.randomUUID();
        BigDecimal amountBrl = new BigDecimal("100.00");
        BigDecimal amountUsd = new BigDecimal("50.00");
        BigDecimal exchangeRate = new BigDecimal("2.00");
        LocalDate date = LocalDate.now();
        TransactionStatusEnum status = TransactionStatusEnum.PENDING;

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionId(transactionId);
        transactionDTO.setWalletId(walletId);
        transactionDTO.setFromUserId(fromUserId);
        transactionDTO.setToUserId(toUserId);
        transactionDTO.setAmountBrl(amountBrl);
        transactionDTO.setAmountUsd(amountUsd);
        transactionDTO.setExchangeRate(exchangeRate);
        transactionDTO.setDate(date);
        transactionDTO.setStatus(status);

        Wallet fromWallet = new Wallet(walletId, fromUserId, new BigDecimal("500.00"), new BigDecimal("200.00"));

        when(walletRepository.findByUserId(fromUserId)).thenReturn(Optional.of(fromWallet));

        walletEventConsumer.processWalletEvent(transactionDTO);

        verify(walletRepository).findByUserId(fromUserId);
    }

    @Test
    @DisplayName("Deve lidar com Wallet não encontrada")
    void testProcessWalletEventWalletNotFound() {
        UUID fromUserId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        UUID toUserId = UUID.randomUUID();
        BigDecimal amountBrl = new BigDecimal("100.00");
        BigDecimal amountUsd = new BigDecimal("50.00");
        BigDecimal exchangeRate = new BigDecimal("2.00");
        LocalDate date = LocalDate.now();
        TransactionStatusEnum status = TransactionStatusEnum.PENDING;

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setWalletId(walletId);
        transactionDTO.setFromUserId(fromUserId);
        transactionDTO.setToUserId(toUserId);
        transactionDTO.setAmountBrl(amountBrl);
        transactionDTO.setAmountUsd(amountUsd);
        transactionDTO.setExchangeRate(exchangeRate);
        transactionDTO.setDate(date);
        transactionDTO.setStatus(status);

        when(walletRepository.findByUserId(fromUserId)).thenReturn(Optional.empty());

        walletEventConsumer.processWalletEvent(transactionDTO);

        verify(walletRepository).findByUserId(fromUserId);
        verifyNoMoreInteractions(walletRepository);
    }

    @Test
    @DisplayName("Deve lidar com saldo insuficiente na Wallet")
    void testProcessWalletEventInsufficientBalance() {
        UUID walletId = UUID.randomUUID();
        UUID fromUserId = UUID.randomUUID();
        UUID toUserId = UUID.randomUUID();
        BigDecimal amountBrl = new BigDecimal("1000.00");
        BigDecimal amountUsd = BigDecimal.ZERO;
        BigDecimal exchangeRate = BigDecimal.ZERO;
        LocalDate date = LocalDate.now();
        TransactionStatusEnum status = TransactionStatusEnum.PENDING;

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setWalletId(walletId);
        transactionDTO.setFromUserId(fromUserId);
        transactionDTO.setToUserId(toUserId);
        transactionDTO.setAmountBrl(amountBrl);
        transactionDTO.setAmountUsd(amountUsd);
        transactionDTO.setExchangeRate(exchangeRate);
        transactionDTO.setDate(date);
        transactionDTO.setStatus(status);

        Wallet fromWallet = new Wallet(walletId, new BigDecimal("500.00"), BigDecimal.ZERO);

        when(walletRepository.findByUserId(fromUserId)).thenReturn(Optional.of(fromWallet));

        walletEventConsumer.processWalletEvent(transactionDTO);

        verify(walletRepository).findByUserId(fromUserId);
    }
}