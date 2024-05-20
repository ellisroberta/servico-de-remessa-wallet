package com.example.servicoderemessawallet.service;

//import com.example.servicoderemessawallet.exception.UserNotFoundException;
import com.example.servicoderemessawallet.model.Transaction;
//import com.example.servicoderemessawallet.model.User;
import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.repository.TransactionRepository;
//import com.example.servicoderemessawallet.repository.UserRepository;
import com.example.servicoderemessawallet.repository.WalletRepository;
//import com.example.servicoderemessawallet.utils.enums.UserTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

//    @Mock
//    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

//    private User fromUser;
//    private User toUser;
    private Wallet fromWallet;
    private Wallet toWallet;

    @BeforeEach
    public void setUp() {
        fromWallet = new Wallet();
        fromWallet.setBalanceBrl(new BigDecimal("10000"));

        toWallet = new Wallet();
        toWallet.setBalanceUsd(new BigDecimal("1000"));

//        fromUser = new User();
//        fromUser.setId(UUID.randomUUID());
//        fromUser.setUserType(UserTypeEnum.PF);
//        fromUser.setWallet(fromWallet);
//
//        toUser = new User();
//        toUser.setId(UUID.randomUUID());
//        toUser.setUserType(UserTypeEnum.PF);
//        toUser.setWallet(toWallet);
    }

//    @Test
//    void testCreateTransaction() {
//        BigDecimal amountBrl = new BigDecimal("500");
//        BigDecimal exchangeRate = new BigDecimal("5.0");

//        when(userRepository.findById(fromUser.getId())).thenReturn(Optional.of(fromUser));
//        when(userRepository.findById(toUser.getId())).thenReturn(Optional.of(toUser));
//        when(transactionRepository.sumTransactionsByUserAndDate(any(), any())).thenReturn(BigDecimal.ZERO);
//
//        Transaction mockTransaction = new Transaction();
//        mockTransaction.setFromUser(fromUser);
//        mockTransaction.setToUser(toUser);
//        mockTransaction.setAmountBrl(amountBrl);
//        mockTransaction.setAmountUsd(amountBrl.divide(exchangeRate, 2, RoundingMode.HALF_UP));
//        mockTransaction.setExchangeRate(exchangeRate);
//        mockTransaction.setDate(LocalDateTime.now());
//
//        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

//        Transaction result = transactionService.createTransaction(fromUser.getId(), toUser.getId(), amountBrl, exchangeRate);
//
//        assertEquals(fromUser, result.getFromUser());
//        assertEquals(toUser, result.getToUser());
//        assertEquals(amountBrl, result.getAmountBrl());
//        assertEquals(amountBrl.divide(exchangeRate, 2, RoundingMode.HALF_UP), result.getAmountUsd());
//        assertEquals(exchangeRate, result.getExchangeRate());
//    }

//    @Test
//    void testCreateTransaction_UserNotFound() {
//        UUID invalidUserId = UUID.randomUUID();
//
//        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());
//
//        assertThrows(UserNotFoundException.class, () -> {
//            transactionService.createTransaction(invalidUserId, toUser.getId(), new BigDecimal("500"), new BigDecimal("5.0"));
//        });
//    }
}