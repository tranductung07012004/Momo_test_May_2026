package com.me.managebill.repository;

import java.util.*;

import com.me.managebill.entity.PaymentTransaction;

public interface PaymentTransactionRepository {
    void save(PaymentTransaction transaction);
    List<PaymentTransaction> findAll();
}
