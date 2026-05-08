package com.me.managebill.repository.impl;

import java.util.*;

import com.me.managebill.entity.PaymentTransaction;
import com.me.managebill.repository.PaymentTransactionRepository;

public class PaymentTransactionRepositoryImpl implements PaymentTransactionRepository {
    private final List<PaymentTransaction> transactions = new ArrayList<>();

    @Override
    public void save(PaymentTransaction transaction) {
        transactions.add(transaction);
    }

    @Override
    public List<PaymentTransaction> findAll() {
        return new ArrayList<>(transactions);
    }
}
