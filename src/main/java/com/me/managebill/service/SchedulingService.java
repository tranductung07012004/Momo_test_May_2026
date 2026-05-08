package com.me.managebill.service;

import java.time.LocalDate;
import java.util.*;

import com.me.managebill.AppException;
import com.me.managebill.entity.Bill;
import com.me.managebill.entity.ScheduledPayment;
import com.me.managebill.enums.BillStatus;
import com.me.managebill.repository.ScheduledPaymentRepository;

public class SchedulingService {
    private final ScheduledPaymentRepository scheduledPaymentRepository;
    private final BillService billService;
    private final PaymentService paymentService;
    private int scheduleSeq = 1;

    public SchedulingService(
            ScheduledPaymentRepository sPRepo,
            BillService bS,
            PaymentService pS) {
        this.scheduledPaymentRepository = sPRepo;
        this.billService = bS;
        this.paymentService = pS;
    }

    public ScheduledPayment schedulePayment(String billId, LocalDate scheduledDate) {
        Bill bill = billService.getBillOrThrow(billId);
        if (bill.status() == BillStatus.PAID) {
            throw new AppException("This bill with id: " + billId + "is paid, so not schedule");
        }
        if (scheduledDate.isBefore(LocalDate.now())) {
            throw new AppException("Scheduled date is in the past. Failed.");
        }
        scheduledPaymentRepository.findActiveByBillIdAndDate(billId, scheduledDate)
                .ifPresent(existing -> {
                    throw new AppException("Duplicate schedule for bill/date.");
                });
        ScheduledPayment sp = new ScheduledPayment(
                String.format("id-%04d", scheduleSeq++),
                billId,
                scheduledDate,
                false);
        return scheduledPaymentRepository.save(sp);
    }

    public int executeDuePayments(LocalDate currentDate) {
        List<ScheduledPayment> res = scheduledPaymentRepository.findPendingOnOrBefore(currentDate);
        for (ScheduledPayment sp : res) {
            paymentService.tryScheduledPayment(sp.billId());
            scheduledPaymentRepository.markExecuted(sp.id());
        }
        return res.size();
    }
}
