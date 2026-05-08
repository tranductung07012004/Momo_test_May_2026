package com.me.managebill.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.me.managebill.AppException;
import com.me.managebill.enums.BillStatus;
import com.me.managebill.repository.impl.BillRepositoryImpl;
import com.me.managebill.repository.impl.PaymentTransactionRepositoryImpl;
import com.me.managebill.repository.impl.ScheduledPaymentRepositoryImpl;
import com.me.managebill.repository.impl.WalletRepositoryImpl;

class SchedulingServiceTest {
    private BillService billService;
    private WalletService walletService;
    private PaymentService paymentService;
    private SchedulingService schedulingService;

    @BeforeEach
    void setUp() {
        billService = new BillService(new BillRepositoryImpl());
        walletService = new WalletService(new WalletRepositoryImpl());
        paymentService = new PaymentService(billService, walletService, new PaymentTransactionRepositoryImpl());
        schedulingService = new SchedulingService(new ScheduledPaymentRepositoryImpl(), billService, paymentService);
    }

    @Test
    void dosomethingnormal() {
        walletService.cashIn(new BigDecimal("100"));
        var bill = billService.createBill("NET", new BigDecimal("60"), LocalDate.now().plusDays(3), "FPT");
        schedulingService.schedulePayment(bill.id(), LocalDate.now());

        int count = schedulingService.executeDuePayments(LocalDate.now());

        assertEquals(1, count);
        assertEquals(BillStatus.PAID, billService.getBillOrThrow(bill.id()).status());
    }

    @Test
    void failtransaction() {
        var bill = billService.createBill("NET", new BigDecimal("60"), LocalDate.now().plusDays(3), "FPT");
        schedulingService.schedulePayment(bill.id(), LocalDate.now());

        schedulingService.executeDuePayments(LocalDate.now());

        assertEquals(BillStatus.UNPAID, billService.getBillOrThrow(bill.id()).status());
    }

    @Test
    void rejectscheduleinpast() {
        var bill = billService.createBill("NET", new BigDecimal("60"), LocalDate.now().plusDays(3), "FPT");

        assertThrows(AppException.class, () -> schedulingService.schedulePayment(bill.id(), LocalDate.now().minusDays(1)));
    }

    @Test
    void rejectduplicate() {
        var bill = billService.createBill("NET", new BigDecimal("60"), LocalDate.now().plusDays(3), "FPT");
        LocalDate scheduledDate = LocalDate.now().plusDays(1);
        schedulingService.schedulePayment(bill.id(), scheduledDate);

        assertThrows(AppException.class, () -> schedulingService.schedulePayment(bill.id(), scheduledDate));
    }
}
