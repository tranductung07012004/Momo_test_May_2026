package com.me.managebill.repository.impl;

import java.time.LocalDate;
import java.util.*;

import com.me.managebill.entity.ScheduledPayment;
import com.me.managebill.repository.ScheduledPaymentRepository;

public class ScheduledPaymentRepositoryImpl implements ScheduledPaymentRepository {
    private final Map<String, ScheduledPayment> store = new LinkedHashMap<>();

    @Override
    public ScheduledPayment save(ScheduledPayment scheduledPayment) {
        store.put(scheduledPayment.id(), scheduledPayment);
        return scheduledPayment;
    }

    @Override
    public List<ScheduledPayment> findPendingOnOrBefore(LocalDate date) {
        List<ScheduledPayment> result = new ArrayList<>();
        for (ScheduledPayment payment : store.values()) {
            if (!payment.executed() && !payment.scheduledDate().isAfter(date)) {
                result.add(payment);
            }
        }
        return result;
    }

    @Override
    public Optional<ScheduledPayment> findActiveByBillIdAndDate(String billId, LocalDate date) {
        return store.values().stream()
                .filter(payment -> !payment.executed()
                        && payment.billId().equals(billId)
                        && payment.scheduledDate().equals(date))
                .findFirst();
    }

    @Override
    public void markExecuted(String id) {
        ScheduledPayment existing = store.get(id);
        if (existing != null) {
            store.put(id, existing.markExecuted());
        }
    }
}
