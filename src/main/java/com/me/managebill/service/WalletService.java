package com.me.managebill.service;

import java.math.BigDecimal;

import com.me.managebill.AppException;
import com.me.managebill.Money;
import com.me.managebill.repository.WalletRepository;

public class WalletService {
    private final WalletRepository repo;

    public WalletService(WalletRepository wRepo) {
        this.repo = wRepo;
    }

    public Money cashIn(BigDecimal amount) {
        ifNotNullAndBiggerThanZero(amount);
        Money incoming = Money.of(amount);
        Money next = repo.getBalance().add(incoming);
        repo.setBalance(next);
        return next;
    }

    public Money getBalance() {
        return repo.getBalance();
    }

    public void subtract(Money amount) {
        if (repo.getBalance().isLessThan(amount)) {
            throw new AppException("Insufficient balance.");
        }
        repo.setBalance(repo.getBalance().subtract(amount));
    }

    private void ifNotNullAndBiggerThanZero(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException("Amount must be greater than zero.");
        }
    }
}
