package com.me.managebill.repository;

import com.me.managebill.Money;

public interface WalletRepository {
    Money getBalance();
    void setBalance(Money balance);
}
