package com.me.managebill.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.me.managebill.AppException;
import com.me.managebill.enums.BillStatus;
import com.me.managebill.enums.PaymentChannel;
import com.me.managebill.repository.impl.BillRepositoryImpl;
import com.me.managebill.repository.impl.PaymentTransactionRepositoryImpl;
import com.me.managebill.repository.impl.WalletRepositoryImpl;
import com.me.managebill.service.BillService;
import com.me.managebill.service.PaymentService;
import com.me.managebill.service.WalletService;

class PaymentServiceTest {
    private BillService billService;
    private WalletService walletService;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        billService = new BillService(new BillRepositoryImpl());
        walletService = new WalletService(new WalletRepositoryImpl());
        paymentService = new PaymentService(billService, walletService, new PaymentTransactionRepositoryImpl());
    }

    @Test
    void canpaybill() {
        var bill = billService.createBill("WATER", new BigDecimal("50"), LocalDate.now().plusDays(1), "PWA");
        walletService.cashIn(new BigDecimal("100"));

        paymentService.paySingle(bill.id(), PaymentChannel.MANUAL);

        assertEquals(BillStatus.PAID, billService.getBillOrThrow(bill.id()).status());
        assertEquals("50.00", walletService.getBalance().toString());
    }

    @Test
    void canpaymultiple() {
        walletService.cashIn(new BigDecimal("100"));
        var b1 = billService.createBill("A", new BigDecimal("80"), LocalDate.now().plusDays(1), "P1");
        var b2 = billService.createBill("B", new BigDecimal("40"), LocalDate.now().plusDays(2), "P2");
        var b3 = billService.createBill("C", new BigDecimal("20"), LocalDate.now().plusDays(3), "P3");

        PaymentService.BatchPaymentResult result = paymentService.payMultiple(List.of(b3.id(), b2.id(), b1.id()));

        assertEquals(List.of(b1.id(), b3.id()), result.paidBillIds());
        assertEquals(List.of(b2.id() + " (insufficient balance)"), result.skippedBillIds());
    }

    @Test
    void canignoreduplicate() {
        walletService.cashIn(new BigDecimal("200"));
        var bill = billService.createBill("WATER", new BigDecimal("50"), LocalDate.now().plusDays(1), "PWA");

        PaymentService.BatchPaymentResult result = paymentService.payMultiple(List.of(bill.id(), bill.id(), bill.id()));

        assertEquals(List.of(bill.id()), result.paidBillIds());
        assertEquals(List.of(), result.skippedBillIds());
        assertEquals("150.00", walletService.getBalance().toString());
    }

    @Test
    void canthrowerror() {
        walletService.cashIn(new BigDecimal("100"));
        var bill = billService.createBill("PHONE", new BigDecimal("30"), LocalDate.now().plusDays(1), "VTL");
        paymentService.paySingle(bill.id(), PaymentChannel.MANUAL);

        assertThrows(AppException.class, () -> paymentService.paySingle(bill.id(), PaymentChannel.MANUAL));
    }
}
