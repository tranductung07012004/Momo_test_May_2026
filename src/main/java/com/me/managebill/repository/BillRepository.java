package com.me.managebill.repository;

import java.util.*;

import com.me.managebill.entity.Bill;

public interface BillRepository {
    Bill save(Bill bill);
    Optional<Bill> findById(String id);
    List<Bill> findAll();
    void deleteById(String id);
}
