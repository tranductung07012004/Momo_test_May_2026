package com.me.managebill.entity;

import java.time.LocalDateTime;

import com.me.managebill.Money;
import com.me.managebill.enums.PaymentChannel;
import com.me.managebill.enums.PaymentStatus;

public final class PaymentTransaction {
    private final String id;
    private final String billId;
    private final Money amount;
    private final LocalDateTime createdAt;
    private final PaymentStatus status;
    private final PaymentChannel channel;
    private final String message;

    public PaymentTransaction(
            String id,
            String billId,
            Money amount,
            LocalDateTime createdAt,
            PaymentStatus status,
            PaymentChannel channel,
            String message) {
        this.id = id;
        this.billId = billId;
        this.amount = amount;
        this.createdAt = createdAt;
        this.status = status;
        this.channel = channel;
        this.message = message;
    }

    public String id() { return id; }
    public String billId() { return billId; }
    public Money amount() { return amount; }
    public LocalDateTime createdAt() { return createdAt; }
    public PaymentStatus status() { return status; }
    public PaymentChannel channel() { return channel; }
    public String message() { return message; }
}
