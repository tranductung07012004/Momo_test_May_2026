package com.me.managebill.service;

import java.time.LocalDateTime;
import java.util.*;

import com.me.managebill.AppException;
import com.me.managebill.entity.Bill;
import com.me.managebill.entity.PaymentTransaction;
import com.me.managebill.enums.BillStatus;
import com.me.managebill.enums.PaymentChannel;
import com.me.managebill.enums.PaymentStatus;
import com.me.managebill.repository.PaymentTransactionRepository;

public class PaymentService {
    private final BillService billService;
    private final WalletService walletService;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private int cnt = 1;

    public PaymentService(
            BillService bS,
            WalletService wS,
            PaymentTransactionRepository pTRepo) {
        this.billService = bS;
        this.walletService = wS;
        this.paymentTransactionRepository = pTRepo;
    }

    public PaymentTransaction paySingle(String billId, PaymentChannel channel) {
        Bill bill = billService.getBillOrThrow(billId);
        if (bill.status() == BillStatus.PAID) {
            throw new AppException("Bill already paid: " + billId);
        }
        walletService.subtract(bill.amount());
        billService.markPaid(billId);
        PaymentTransaction res = createTransaction(billId, bill.amount(), PaymentStatus.SUCCESS, channel, "Paid successfully");
        paymentTransactionRepository.save(res);
        return res;
    }

    public BatchPaymentResult payMultiple(List<String> billIds) {
        if (billIds == null || billIds.isEmpty()) {
            throw new AppException("At least one bill id is required.");
        }
        Set<String> uniqueIds = new LinkedHashSet<>(billIds);
        List<Bill> targetBills = uniqueIds.stream()
                .map(billService::getBillOrThrow)
                .filter(b -> b.status() == BillStatus.UNPAID)
                .sorted(Comparator.comparing(Bill::dueDate).thenComparing(Bill::id))
                .toList();

        List<String> paid = new ArrayList<>();
        List<String> skipped = new ArrayList<>();

        for (Bill bill : targetBills) {
            if (walletService.getBalance().isLessThan(bill.amount())) {
                skipped.add(bill.id() + " (insufficient balance)");
                paymentTransactionRepository.save(createTransaction(
                        bill.id(),
                        bill.amount(),
                        PaymentStatus.FAILED,
                        PaymentChannel.BATCH,
                        "Skipped due to insufficient balance"));
                continue;
            }
            walletService.subtract(bill.amount());
            billService.markPaid(bill.id());
            paid.add(bill.id());
            paymentTransactionRepository.save(createTransaction(
                    bill.id(),
                    bill.amount(),
                    PaymentStatus.SUCCESS,
                    PaymentChannel.BATCH,
                    "Paid in batch"));
        }
        return new BatchPaymentResult(paid, skipped);
    }

    public PaymentTransaction tryScheduledPayment(String billId) {
        Bill bill = billService.getBillOrThrow(billId);
        if (bill.status() == BillStatus.PAID) {
            PaymentTransaction res = createTransaction(
                    billId, bill.amount(), PaymentStatus.FAILED, PaymentChannel.SCHEDULED, "Bill already paid");
            paymentTransactionRepository.save(res);
            return res;
        }
        if (walletService.getBalance().isLessThan(bill.amount())) {
            PaymentTransaction res = createTransaction(
                    billId, bill.amount(), PaymentStatus.FAILED, PaymentChannel.SCHEDULED, "Insufficient balance");
            paymentTransactionRepository.save(res);
            return res;
        }
        walletService.subtract(bill.amount());
        billService.markPaid(billId);
        PaymentTransaction res = createTransaction(
                billId, bill.amount(), PaymentStatus.SUCCESS, PaymentChannel.SCHEDULED, "Scheduled payment success");
        paymentTransactionRepository.save(res);
        return res;
    }

    private PaymentTransaction createTransaction(
            String billId,
            com.me.managebill.Money amount,
            PaymentStatus status,
            PaymentChannel channel,
            String message) {
        return new PaymentTransaction(
                String.format("id-%04d", cnt++),
                billId,
                amount,
                LocalDateTime.now(),
                status,
                channel,
                message);
    }

    public record BatchPaymentResult(List<String> paidBillIds, List<String> skippedBillIds) {
    }
}
