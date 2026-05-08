package com.me.managebill.repository.impl;

import com.me.managebill.Money;
import com.me.managebill.repository.WalletRepository;

public class WalletRepositoryImpl implements WalletRepository {
    private Money balance = Money.zero();

    @Override
    public Money getBalance() {
        return balance;
    }

    @Override
    public void setBalance(Money balance) {
        this.balance = balance;
    }
}
