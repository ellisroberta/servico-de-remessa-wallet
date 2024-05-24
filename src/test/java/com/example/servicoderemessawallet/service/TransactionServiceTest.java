package com.example.servicoderemessawallet.service;

import com.example.servicoderemessawallet.dto.TransactionDTO;
import com.example.servicoderemessawallet.exception.InsufficientBalanceException;
import com.example.servicoderemessawallet.exception.WalletNotFoundException;
import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.repository.TransactionRepository;
import com.example.servicoderemessawallet.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("Teste de criação de transação - Sucesso")
    void testCreateTransactionSuccess() {
        // Given
        UUID fromUserId = UUID.randomUUID();
        UUID toUserId = UUID.randomUUID();
        BigDecimal amountBrl = BigDecimal.TEN;
        BigDecimal exchangeRate = BigDecimal.valueOf(5.0); // Exemplo de taxa de câmbio
        BigDecimal amountUsd = amountBrl.divide(exchangeRate, 2, BigDecimal.ROUND_HALF_UP);

        Wallet fromWallet = Wallet.builder()
                .id(UUID.randomUUID())
                .userId(fromUserId)
                .balanceBrl(BigDecimal.valueOf(20.0))
                .balanceUsd(BigDecimal.ZERO)
                .build();

        Wallet toWallet = Wallet.builder()
                .id(UUID.randomUUID())
                .userId(toUserId)
                .balanceBrl(BigDecimal.ZERO)
                .balanceUsd(BigDecimal.ZERO)
                .build();

        // Mocks
        when(walletRepository.findByUserId(fromUserId)).thenReturn(Optional.of(fromWallet));
        when(walletRepository.findByUserId(toUserId)).thenReturn(Optional.of(toWallet));
        when(exchangeRateService.getDollarExchangeRate()).thenReturn(exchangeRate);

        // When
        transactionService.createAndSendTransaction(fromUserId, toUserId, amountBrl);

        // Then
        verify(walletRepository).findByUserId(fromUserId);
        verify(walletRepository).findByUserId(toUserId);
        verify(walletRepository).save(fromWallet);
        verify(walletRepository).save(toWallet);
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(TransactionDTO.class));

        Assertions.assertEquals(BigDecimal.valueOf(10.0), fromWallet.getBalanceBrl());
        Assertions.assertEquals(amountUsd, toWallet.getBalanceUsd());
    }

    @Test
    @DisplayName("Teste de criação de transação - Saldo Insuficiente")
    void testCreateTransactionInsufficientBalance() {
        // Given
        UUID fromUserId = UUID.randomUUID();
        UUID toUserId = UUID.randomUUID();
        BigDecimal amountBrl = BigDecimal.valueOf(30.0); // Valor maior que o saldo disponível

        // Criação da Wallet de origem com saldo insuficiente
        Wallet fromWallet = Wallet.builder()
                .id(UUID.randomUUID())
                .userId(fromUserId)
                .balanceBrl(BigDecimal.TEN) // Saldo insuficiente
                .balanceUsd(BigDecimal.ZERO)
                .build();

        // Criação da Wallet de destino
        Wallet toWallet = Wallet.builder()
                .id(UUID.randomUUID())
                .userId(toUserId)
                .balanceBrl(BigDecimal.ZERO)
                .balanceUsd(BigDecimal.ZERO)
                .build();

        // Configuração do mock do walletRepository para retornar a fromWallet e toWallet
        when(walletRepository.findByUserId(fromUserId)).thenReturn(Optional.of(fromWallet));
        when(walletRepository.findByUserId(toUserId)).thenReturn(Optional.of(toWallet));

        // When/Then
        InsufficientBalanceException exception = Assertions.assertThrows(InsufficientBalanceException.class,
                () -> transactionService.createAndSendTransaction(fromUserId, toUserId, amountBrl),
                "Saldo insuficiente");

        Assertions.assertEquals("Saldo insuficiente na carteira de origem", exception.getMessage());

        // Verificações
        verify(walletRepository).findByUserId(fromUserId);
        verify(walletRepository).findByUserId(toUserId);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(TransactionDTO.class));
    }

    @Test
    @DisplayName("Teste de criação de transação - Carteira não encontrada")
    void testCreateTransactionWalletNotFound() {
        // Given
        UUID fromUserId = UUID.randomUUID();
        UUID toUserId = UUID.randomUUID();
        BigDecimal amountBrl = BigDecimal.TEN;

        when(walletRepository.findByUserId(fromUserId)).thenReturn(Optional.empty());

        // When/Then
        WalletNotFoundException exception = Assertions.assertThrows(WalletNotFoundException.class,
                () -> transactionService.createAndSendTransaction(fromUserId, toUserId, amountBrl),
                "Carteira não encontrada para o usuário: " + fromUserId);

        Assertions.assertEquals("Carteira não encontrada para o usuário: " + fromUserId, exception.getMessage());

        // Verificações
        verify(walletRepository).findByUserId(fromUserId);
        verify(walletRepository, never()).findByUserId(toUserId);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(TransactionDTO.class));
    }
}