package com.example.servicoderemessawallet.service;

//import com.example.servicoderemessawallet.model.User;
import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    private UUID userId;
    //private User user;
    private Wallet wallet;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
//        user = new User();
//        user.setId(userId);

        wallet = new Wallet();
        wallet.setId(UUID.randomUUID());
        //wallet.setUser(user);
        wallet.setBalanceBrl(BigDecimal.ZERO);
        wallet.setBalanceUsd(BigDecimal.ZERO);
    }

    @Test
    public void testCreateWallet_Success() {
        Mockito.when(walletRepository.findByUserId(userId)).thenReturn(null);
        Mockito.when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Wallet createdWallet = walletService.createWallet(userId);

        assertNotNull(createdWallet);
        //assertEquals(user, createdWallet.getUser());
        assertEquals(BigDecimal.ZERO, createdWallet.getBalanceBrl());
        assertEquals(BigDecimal.ZERO, createdWallet.getBalanceUsd());
    }

    @Test
    public void testCreateWallet_UserAlreadyHasWallet() {
        Mockito.when(walletRepository.findByUserId(userId)).thenReturn(wallet);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.createWallet(userId);
        });

        assertEquals("User already has a wallet", exception.getMessage());
    }

    @Test
    public void testFindWalletByUserId_Success() {
        Mockito.when(walletRepository.findByUserId(userId)).thenReturn(wallet);

        Wallet foundWallet = walletService.findWalletByUserId(userId);

        assertNotNull(foundWallet);
        assertEquals(wallet.getId(), foundWallet.getId());
        //assertEquals(userId, foundWallet.getUser().getId());
    }

    @Test
    public void testFindWalletByUserId_WalletNotFound() {
        Mockito.when(walletRepository.findByUserId(any(UUID.class))).thenReturn(null);

        Wallet foundWallet = walletService.findWalletByUserId(userId);

        assertNull(foundWallet);
    }
}