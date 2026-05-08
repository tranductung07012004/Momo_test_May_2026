package com.me.managebill.repository.impl;

import java.util.*;

import com.me.managebill.entity.Bill;
import com.me.managebill.repository.BillRepository;

public class BillRepositoryImpl implements BillRepository {
    private final Map<String, Bill> store = new LinkedHashMap<>();

    @Override
    public Bill save(Bill bill) {
        store.put(bill.id(), bill);
        return bill;
    }

    @Override
    public Optional<Bill> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Bill> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }
}
