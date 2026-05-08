package com.me.managebill.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.me.managebill.AppException;
import com.me.managebill.Money;
import com.me.managebill.repository.impl.WalletRepositoryImpl;
import com.me.managebill.service.WalletService;

class WalletServiceTest {
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        walletService = new WalletService(new WalletRepositoryImpl());
    }

    @Test
    void invalidcashamount() {
        assertThrows(AppException.class, () -> walletService.cashIn(BigDecimal.ZERO));
        assertThrows(AppException.class, () -> walletService.cashIn(new BigDecimal("-1")));
    }

    @Test
    void rejectsubtractmoneywhennotenoughmoney() {
        walletService.cashIn(new BigDecimal("10"));

        assertThrows(AppException.class, () -> walletService.subtract(Money.of(new BigDecimal("20"))));
        assertEquals("10.00", walletService.getBalance().toString());
    }
}
