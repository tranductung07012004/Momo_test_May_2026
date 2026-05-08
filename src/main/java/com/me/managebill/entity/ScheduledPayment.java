package com.me.managebill.entity;

import java.time.LocalDate;

public final class ScheduledPayment {
    private final String id;
    private final String billId;
    private final LocalDate scheduledDate;
    private final boolean executed;

    public ScheduledPayment(String id, String billId, LocalDate scheduledDate, boolean isExecuted) {
        this.id = id;
        this.billId = billId;
        this.scheduledDate = scheduledDate;
        this.executed = isExecuted;
    }

    public String id() { return id; }
    public String billId() { return billId; }
    public LocalDate scheduledDate() { return scheduledDate; }
    public boolean executed() { return executed; }

    public ScheduledPayment markExecuted() {
        return new ScheduledPayment(id, billId, scheduledDate, true);
    }
}
