package com.example.servicoderemessawallet.event;

import com.example.servicoderemessawallet.enums.TransactionStatusEnum;
import com.example.servicoderemessawallet.messaging.WalletEventConsumer;
import com.example.servicoderemessawallet.model.Transaction;
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
public class WalletEventConsumerTest {

        @Mock
        private TransactionRepository transactionRepository;

        @Mock
        private WalletRepository walletRepository;

        @InjectMocks
        private WalletEventConsumer walletEventConsumer;

        @Test
        @DisplayName("Deve processar transação com sucesso")
        public void testProcessWalletEventSuccess() {
            // Mock da transação recebida
            UUID transactionId = UUID.randomUUID();
            UUID walletId = UUID.randomUUID();
            UUID fromUserId = UUID.randomUUID();
            UUID toUserId = UUID.randomUUID();
            BigDecimal amountBrl = new BigDecimal("100.00");
            BigDecimal amountUsd = new BigDecimal("50.00");
            TransactionStatusEnum status = TransactionStatusEnum.PENDING;

            Transaction transaction = Transaction.builder()
                    .walletId(walletId)
                    .fromUserId(fromUserId)
                    .toUserId(toUserId)
                    .amountBrl(amountBrl)
                    .amountUsd(amountUsd)
                    .status(status)
                    .build();

            transaction.setId(transactionId); // Define o ID da transação manualmente (normalmente gerado pelo banco de dados)
            transaction.setDate(LocalDate.now()); // Define a data atual

            // Mock da Wallet de origem
            Wallet fromWallet = new Wallet(walletId, new BigDecimal("500.00"), new BigDecimal("200.00"));

            // Mock do Optional retornado pelo repository
            when(transactionRepository.existsById(transactionId)).thenReturn(true);

            // Executa o método sob teste
            walletEventConsumer.processWalletEvent(transaction);

            // Verificações
            verify(transactionRepository).existsById(transactionId); // Verifica se o método do repositório foi chamado com os parâmetros corretos
        }

        @Test
        @DisplayName("Deve lidar com Wallet não encontrada")
        public void testProcessWalletEventWalletNotFound() {
            // Mock da transação recebida
            UUID fromUserId = UUID.randomUUID();
            UUID walletId = UUID.randomUUID();
            Transaction transaction = Transaction.builder()
                    .walletId(walletId)
                    .fromUserId(fromUserId)
                    .toUserId(UUID.randomUUID())
                    .amountBrl(new BigDecimal("100.00"))
                    .amountUsd(new BigDecimal("50.00"))
                    .status(TransactionStatusEnum.PENDING)
                    .build();

            transaction.setDate(LocalDate.now()); // Define a data atual

            // Mock do Optional retornado pelo repository (vazio)
            when(walletRepository.findByUserId(fromUserId)).thenReturn(Optional.empty());

            // Executa o método sob teste
            walletEventConsumer.processWalletEvent(transaction);

            // Verificações
            verify(walletRepository).findByUserId(fromUserId);
            verifyNoMoreInteractions(walletRepository); // Nenhuma interação adicional com o repository de wallets
        }

        @Test
        @DisplayName("Deve lidar com saldo insuficiente na Wallet")
        public void testProcessWalletEventInsufficientBalance() {
            // Mock da transação recebida
            UUID walletId = UUID.randomUUID();
            UUID fromUserId = UUID.randomUUID();
            Transaction transaction = Transaction.builder()
                    .walletId(walletId)
                    .fromUserId(fromUserId)
                    .toUserId(UUID.randomUUID())
                    .amountBrl(new BigDecimal("1000.00"))
                    .amountUsd(BigDecimal.ZERO)
                    .status(TransactionStatusEnum.PENDING)
                    .build();
            transaction.setDate(LocalDate.now()); // Define a data atual

            // Mock da Wallet de origem
            Wallet fromWallet = new Wallet(walletId, new BigDecimal("500.00"), BigDecimal.ZERO);

            // Mock do Optional retornado pelo repository
            when(walletRepository.findByUserId(fromUserId)).thenReturn(Optional.of(fromWallet));

            // Executa o método sob teste
            walletEventConsumer.processWalletEvent(transaction);

            // Verificações
            verify(walletRepository).findByUserId(fromUserId);
            verifyNoMoreInteractions(walletRepository); // Nenhuma interação adicional com o repository de wallets
        }

}
