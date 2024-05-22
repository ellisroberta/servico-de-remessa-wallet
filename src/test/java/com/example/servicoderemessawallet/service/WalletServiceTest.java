package com.example.servicoderemessawallet.service;

import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    @DisplayName("Teste do método getWalletByUserId - Encontrado")
    public void testGetWalletByUserIdFound() {
        // Given
        UUID userId = UUID.randomUUID();
        Wallet wallet = Wallet.builder()
                .id(UUID.randomUUID())
                .balanceBrl(BigDecimal.ZERO)
                .balanceUsd(BigDecimal.ZERO)
                .build();
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        // When
        Optional<Wallet> foundWallet = walletService.getWalletByUserId(userId);

        // Then
        Assertions.assertTrue(foundWallet.isPresent());
        Assertions.assertEquals(wallet.getId(), foundWallet.get().getId());
        Assertions.assertEquals(wallet.getBalanceBrl(), foundWallet.get().getBalanceBrl());
        Assertions.assertEquals(wallet.getBalanceUsd(), foundWallet.get().getBalanceUsd());

        verify(walletRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Teste do método getWalletByUserId - Não encontrado")
    public void testGetWalletByUserIdNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When
        Optional<Wallet> foundWallet = walletService.getWalletByUserId(userId);

        // Then
        Assertions.assertTrue(foundWallet.isEmpty());

        verify(walletRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Teste do método getAllWallets")
    public void testGetAllWallets() {
        // Given
        Wallet wallet1 = Wallet.builder()
                .id(UUID.randomUUID())
                .balanceBrl(BigDecimal.ZERO)
                .balanceUsd(BigDecimal.ZERO)
                .build();
        Wallet wallet2 = Wallet.builder()
                .id(UUID.randomUUID())
                .balanceBrl(BigDecimal.TEN)
                .balanceUsd(BigDecimal.ONE)
                .build();
        List<Wallet> wallets = Arrays.asList(wallet1, wallet2);
        when(walletRepository.findAll()).thenReturn(wallets);

        // When
        List<Wallet> foundWallets = walletService.getAllWallets();

        // Then
        Assertions.assertEquals(2, foundWallets.size());
        Assertions.assertEquals(wallet1.getId(), foundWallets.get(0).getId());
        Assertions.assertEquals(wallet2.getId(), foundWallets.get(1).getId());
        Assertions.assertEquals(wallet1.getBalanceBrl(), foundWallets.get(0).getBalanceBrl());
        Assertions.assertEquals(wallet2.getBalanceUsd(), foundWallets.get(1).getBalanceUsd());

        verify(walletRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Teste do método createWallet")
    public void testCreateWallet() {
        // Given
        Wallet walletToSave = Wallet.builder()
                .balanceBrl(BigDecimal.ZERO)
                .balanceUsd(BigDecimal.ZERO)
                .build();
        Wallet savedWallet = Wallet.builder()
                .id(UUID.randomUUID())
                .balanceBrl(BigDecimal.ZERO)
                .balanceUsd(BigDecimal.ZERO)
                .build();
        when(walletRepository.save(any(Wallet.class))).thenReturn(savedWallet);

        // When
        Wallet createdWallet = walletService.createWallet(walletToSave);

        // Then
        Assertions.assertNotNull(createdWallet.getId());
        Assertions.assertEquals(walletToSave.getBalanceBrl(), createdWallet.getBalanceBrl());
        Assertions.assertEquals(walletToSave.getBalanceUsd(), createdWallet.getBalanceUsd());

        verify(walletRepository, times(1)).save(eq(walletToSave));
    }

    @Test
    @DisplayName("Teste do método updateWallet - Encontrado")
    public void testUpdateWalletFound() {
        // Given
        UUID walletId = UUID.randomUUID();
        Wallet existingWallet = Wallet.builder()
                .id(walletId)
                .balanceBrl(BigDecimal.ZERO)
                .balanceUsd(BigDecimal.ZERO)
                .build();
        Wallet updatedWallet = Wallet.builder()
                .id(walletId)
                .balanceBrl(BigDecimal.TEN)
                .balanceUsd(BigDecimal.ONE)
                .build();
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(updatedWallet);

        // When
        Optional<Wallet> updatedOptional = walletService.updateWallet(walletId, updatedWallet);

        // Then
        Assertions.assertTrue(updatedOptional.isPresent());
        Wallet updated = updatedOptional.get();
        Assertions.assertEquals(existingWallet.getId(), updated.getId());
        Assertions.assertEquals(updatedWallet.getBalanceBrl(), updated.getBalanceBrl());
        Assertions.assertEquals(updatedWallet.getBalanceUsd(), updated.getBalanceUsd());

        verify(walletRepository, times(1)).findById(walletId);
        verify(walletRepository, times(1)).save(eq(existingWallet));
    }

    @Test
    @DisplayName("Teste do método updateWallet - Não encontrado")
    public void testUpdateWalletNotFound() {
        // Given
        UUID walletId = UUID.randomUUID();
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // When
        Optional<Wallet> updatedOptional = walletService.updateWallet(walletId, Wallet.builder().build());

        // Then
        Assertions.assertTrue(updatedOptional.isEmpty());

        verify(walletRepository, times(1)).findById(walletId);
        verify(walletRepository, never()).save(any());
    }

    @Test
    @DisplayName("Teste do método deleteWallet")
    public void testDeleteWallet() {
        // Given
        UUID walletId = UUID.randomUUID();

        // When
        walletService.deleteWallet(walletId);

        // Then
        verify(walletRepository, times(1)).deleteById(walletId);
    }
}