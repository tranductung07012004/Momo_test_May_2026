package com.me.managebill.repository;

import java.time.LocalDate;
import java.util.*;

import com.me.managebill.entity.ScheduledPayment;

public interface ScheduledPaymentRepository {
    ScheduledPayment save(ScheduledPayment scheduledPayment);
    List<ScheduledPayment> findPendingOnOrBefore(LocalDate date);
    Optional<ScheduledPayment> findActiveByBillIdAndDate(String billId, LocalDate date);
    void markExecuted(String id);
}
