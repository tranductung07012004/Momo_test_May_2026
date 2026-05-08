package com.me.managebill.entity;

import java.time.LocalDate;

import com.me.managebill.Money;
import com.me.managebill.enums.BillStatus;

public final class Bill {
    private final String id;
    private final String type;
    private final Money amount;
    private final LocalDate dueDate;
    private final String provider;
    private final BillStatus status;

    public Bill(String id, String type, Money amount, LocalDate dueDate, String provider, BillStatus status) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.dueDate = dueDate;
        this.provider = provider;
        this.status = status;
    }

    public String id() { return id; }
    public String type() { return type; }
    public Money amount() { return amount; }
    public LocalDate dueDate() { return dueDate; }
    public String provider() { return provider; }
    public BillStatus status() { return status; }

    public Bill markPaid() {
        return new Bill(id, type, amount, dueDate, provider, BillStatus.PAID);
    }

    public Bill update(String type, Money amount, LocalDate dueDate, String provider) {
        return new Bill(id, type, amount, dueDate, provider, status);
    }

}
