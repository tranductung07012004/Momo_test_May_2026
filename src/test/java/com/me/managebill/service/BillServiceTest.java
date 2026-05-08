package com.me.managebill.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.me.managebill.AppException;
import com.me.managebill.repository.impl.BillRepositoryImpl;
import com.me.managebill.service.BillService;

class BillServiceTest {
    private BillService billService;

    @BeforeEach
    void setUp() {
        billService = new BillService(new BillRepositoryImpl());
    }

    @Test
    void dothingsInNornal() {
        billService.createBill("ELECTRIC", new BigDecimal("100"), LocalDate.now().plusDays(1), "EVN");
        assertEquals(1, billService.listBills().size());
    }

    @Test
    void invalid() {
        assertThrows(AppException.class, () ->
                billService.createBill("ELECTRIC", BigDecimal.ZERO, LocalDate.now().plusDays(1), "EVN"));
    }
}
