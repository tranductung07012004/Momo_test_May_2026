package com.me.managebill.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.me.managebill.AppException;
import com.me.managebill.Money;
import com.me.managebill.entity.Bill;
import com.me.managebill.enums.BillStatus;
import com.me.managebill.repository.BillRepository;

public class BillService {
    private final BillRepository billRepository;
    private int cnt = 1;

    public BillService(BillRepository bRepo) {
        this.billRepository = bRepo;
    }

    public Bill createBill(String type, BigDecimal amount, LocalDate dueDate, String provider) {
        isTextValid(type, "type");
        isTextValid(provider, "provider");
        String id = String.format("BILL-%04d", cnt++);
        Bill bill = new Bill(id, type, Money.of(requirePositiveAmount(amount)), dueDate, provider, BillStatus.UNPAID);
        return billRepository.save(bill);
    }

    private void isTextValid(String text, String field) {
        if (text == null || text.isBlank()) {
            throw new AppException(field + " must not be blank.");
        }
    }

    public Bill updateBill(String id, String type, BigDecimal amount, LocalDate dueDate, String provider) {
        Bill existing = getBillOrThrow(id);
        if (existing.status() == BillStatus.PAID) {
            throw new AppException("Paid bill cannot be updated.");
        }
        Bill updated = existing.update(type, Money.of(requirePositiveAmount(amount)), dueDate, provider);
        return billRepository.save(updated);
    }

    public void deleteBill(String id) {
        Bill existing = getBillOrThrow(id);
        if (existing.status() == BillStatus.PAID) {
            throw new AppException("Paid bill cannot be deleted.");
        }
        billRepository.deleteById(id);
    }

    public List<Bill> listBills() {
        return billRepository.findAll().stream()
                .sorted(Comparator.comparing(Bill::dueDate).thenComparing(Bill::id))
                .collect(Collectors.toList());
    }

    public Bill getBillOrThrow(String id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new AppException("Bill not found: " + id));
    }

    public Bill markPaid(String id) {
        Bill current = getBillOrThrow(id);
        if (current.status() == BillStatus.PAID) {
            throw new AppException("Bill already paid: " + id);
        }
        Bill paid = current.markPaid();
        return billRepository.save(paid);
    }

    private BigDecimal requirePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException("Amount must be greater than zero.");
        }
        return amount;
    }
}
