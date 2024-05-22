package com.example.servicoderemessawallet.service;

//import com.example.servicoderemessawallet.exception.UserNotFoundException;

import com.example.servicoderemessawallet.enums.TransactionStatusEnum;
import com.example.servicoderemessawallet.exception.InsufficientBalanceException;
import com.example.servicoderemessawallet.exception.WalletNotFoundException;
import com.example.servicoderemessawallet.model.Transaction;
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

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private ExchangeRateService exchangeRateService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("Teste de criação de transação - Sucesso")
    public void testCreateTransactionSuccess() {
        // Given
        UUID walletId = UUID.randomUUID();
        UUID fromUserId = UUID.randomUUID();
        UUID toUserId = UUID.randomUUID();
        BigDecimal amountBrl = BigDecimal.TEN;
        BigDecimal exchangeRate = BigDecimal.valueOf(5.0); // Exemplo de taxa de câmbio
        BigDecimal amountUsd = amountBrl.divide(exchangeRate, 2, BigDecimal.ROUND_HALF_UP);
        Wallet fromWallet = Wallet.builder()
                .id(UUID.randomUUID())
                .balanceBrl(BigDecimal.valueOf(20.0))
                .balanceUsd(BigDecimal.ZERO)
                .build();
        Wallet toWallet = Wallet.builder()
                .id(UUID.randomUUID())
                .balanceBrl(BigDecimal.ZERO)
                .balanceUsd(BigDecimal.ZERO)
                .build();

        // Mocks
        when(walletRepository.findByUserId(fromUserId)).thenReturn(Optional.of(fromWallet));
        when(walletRepository.findByUserId(toUserId)).thenReturn(Optional.of(toWallet));
        when(exchangeRateService.getDollarExchangeRate()).thenReturn(exchangeRate);
        when(walletRepository.save(any(Wallet.class))).thenReturn(fromWallet, toWallet);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setId(UUID.randomUUID());
            return transaction;
        });

        // When
        Transaction createdTransaction = transactionService.createTransaction(fromUserId, toUserId, amountBrl);

        // Then
        Assertions.assertNotNull(createdTransaction);
        Assertions.assertEquals(fromWallet.getId(), createdTransaction.getWalletId());
        Assertions.assertEquals(fromUserId, createdTransaction.getFromUserId());
        Assertions.assertEquals(toUserId, createdTransaction.getToUserId());
        Assertions.assertEquals(amountBrl, createdTransaction.getAmountBrl());
        Assertions.assertEquals(amountUsd, createdTransaction.getAmountUsd());
        Assertions.assertEquals(exchangeRate, createdTransaction.getExchangeRate());
        Assertions.assertEquals(TransactionStatusEnum.PENDING, createdTransaction.getStatus());

        verify(walletRepository, times(1)).findByUserId(fromUserId);
        verify(walletRepository, times(1)).findByUserId(toUserId);
        verify(walletRepository, times(1)).save(eq(fromWallet));
        verify(walletRepository, times(1)).save(eq(toWallet));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Teste de criação de transação - Saldo Insuficiente")
    public void testCreateTransactionInsufficientBalance() {
        // Given
        UUID fromUserId = UUID.randomUUID();
        UUID toUserId = UUID.randomUUID();
        BigDecimal amountBrl = BigDecimal.valueOf(30.0); // Valor maior que o saldo disponível

        // Criação da Wallet de origem com saldo insuficiente
        Wallet fromWallet = Wallet.builder()
                .id(UUID.randomUUID())
                .balanceBrl(BigDecimal.TEN) // Saldo insuficiente
                .balanceUsd(BigDecimal.ZERO)
                .build();

        // Criação da Wallet de destino
        Wallet toWallet = Wallet.builder()
                .id(UUID.randomUUID())
                .balanceBrl(BigDecimal.ZERO)
                .balanceUsd(BigDecimal.ZERO)
                .build();

        // Configuração do mock do walletRepository para retornar a fromWallet e toWallet
        when(walletRepository.findByUserId(fromUserId)).thenReturn(Optional.of(fromWallet));
        when(walletRepository.findByUserId(toUserId)).thenReturn(Optional.of(toWallet));

        // When/Then
        InsufficientBalanceException exception = Assertions.assertThrows(InsufficientBalanceException.class,
                () -> transactionService.createTransaction(fromUserId, toUserId, amountBrl),
                "Saldo insuficiente");

        Assertions.assertEquals("Saldo insuficiente na carteira de origem", exception.getMessage());

        // Verificações
        verify(walletRepository, times(1)).findByUserId(fromUserId);
        verify(walletRepository, times(1)).findByUserId(toUserId);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Teste de criação de transação - Carteira não encontrada")
    public void testCreateTransactionWalletNotFound() {
        // Given
        UUID fromUserId = UUID.randomUUID();
        UUID toUserId = UUID.randomUUID();
        BigDecimal amountBrl = BigDecimal.TEN;
        when(walletRepository.findByUserId(fromUserId)).thenReturn(Optional.empty());

        // When/Then
        WalletNotFoundException exception = Assertions.assertThrows(WalletNotFoundException.class,
                () -> transactionService.createTransaction(fromUserId, toUserId, amountBrl),
                "Carteira não encontrada para o usuário: " + fromUserId);

        Assertions.assertEquals("Carteira não encontrada para o usuário: " + fromUserId, exception.getMessage());

        // Verificações
        verify(walletRepository, times(1)).findByUserId(fromUserId);
        verify(walletRepository, never()).findByUserId(toUserId);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}