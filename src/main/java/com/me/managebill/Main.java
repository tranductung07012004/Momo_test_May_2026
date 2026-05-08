package com.me.managebill;

import com.me.managebill.repository.impl.BillRepositoryImpl;
import com.me.managebill.repository.impl.PaymentTransactionRepositoryImpl;
import com.me.managebill.repository.impl.ScheduledPaymentRepositoryImpl;
import com.me.managebill.repository.impl.WalletRepositoryImpl;
import com.me.managebill.service.BillService;
import com.me.managebill.service.PaymentService;
import com.me.managebill.service.SchedulingService;
import com.me.managebill.service.WalletService;

public class Main {
    public static void main(String[] args) {
        BillRepositoryImpl billRepository = new BillRepositoryImpl();
        WalletRepositoryImpl walletRepository = new WalletRepositoryImpl();
        PaymentTransactionRepositoryImpl paymentTransactionRepository = new PaymentTransactionRepositoryImpl();
        ScheduledPaymentRepositoryImpl scheduledPaymentRepository = new ScheduledPaymentRepositoryImpl();

        WalletService walletService = new WalletService(walletRepository);
        BillService billService = new BillService(billRepository);
        PaymentService paymentService = new PaymentService(billService, walletService, paymentTransactionRepository);
        SchedulingService schedulingService = new SchedulingService(scheduledPaymentRepository, billService, paymentService);

        cli app =  new cli(walletService, billService, paymentService, schedulingService);

        app.run();
    }
}
