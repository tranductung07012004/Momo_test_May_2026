package com.me.managebill;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import com.me.managebill.entity.Bill;
import com.me.managebill.entity.PaymentTransaction;
import com.me.managebill.entity.ScheduledPayment;
import com.me.managebill.enums.PaymentChannel;
import com.me.managebill.service.BillService;
import com.me.managebill.service.PaymentService;
import com.me.managebill.service.SchedulingService;
import com.me.managebill.service.WalletService;

public class cli {
    private final WalletService walletService;
    private final BillService billService;
    private final PaymentService paymentService;
    private final SchedulingService schedulingService;

    public cli(
            WalletService wS,
            BillService bS,
            PaymentService pS,
            SchedulingService sS) {
        this.walletService = wS;
        this.billService = bS;
        this.paymentService = pS;
        this.schedulingService = sS;
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Input exit or EXIT to stop");
        while (true) {
            try {
                int executed = schedulingService.executeDuePayments(LocalDate.now());
                if (executed > 0) {
                    System.out.println("Auto executed scheduled payments: " + executed);
                }
                System.out.print("> ");
                if (!sc.hasNextLine()) {
                    break;
                }
                String line = sc.nextLine();
                String output = chooseWhichCommandToRunAndRun(line);
                
                if (output.equals("ZZZ")) {
                    System.out.println("See you again.");
                    break;
                } else {
                    System.out.println(output);
                }
            } catch (AppException | IllegalArgumentException ex) {
                System.out.println("err: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("something err");
            }
        }

        sc.close();
    }

    String chooseWhichCommandToRunAndRun(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Command is empty.");
        }

        String[] tokens = line.trim().split("\\s+");
        String command = tokens[0].toUpperCase();
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

        if ("CASH_IN".equals(command)) {
            requireArgs(args, 1, "CASH_IN <amount>");
            return "Balance: " + walletService.cashIn(new BigDecimal(args[0]));
        }
        if ("CREATE_BILL".equals(command)) {
            requireArgs(args, 4, "CREATE_BILL <type> <amount> <dueDate> <provider>");
            Bill bill = billService.createBill(args[0], new BigDecimal(args[1]), DateUtil.parseDate(args[2]), args[3]);
            return "Created bill: " + bill.id();
        }
        if ("UPDATE_BILL".equals(command)) {
            requireArgs(args, 5, "UPDATE_BILL <id> <type> <amount> <dueDate> <provider>");
            Bill bill = billService.updateBill(
                    args[0], args[1], new BigDecimal(args[2]), DateUtil.parseDate(args[3]), args[4]);
            return "Updated bill: " + bill.id();
        }
        if ("DELETE_BILL".equals(command)) {
            requireArgs(args, 1, "DELETE_BILL <id>");
            billService.deleteBill(args[0]);
            return "Deleted bill: " + args[0];
        }
        if ("LIST_BILL".equals(command)) {
            return formatBills(billService.listBills());
        }
        if ("SEARCH_BILL_BY_PROVIDER".equals(command)) {
            requireArgs(args, 1, "SEARCH_BILL_BY_PROVIDER <provider>");
            return formatBills(billService.searchByProvider(args[0]));
        }
        if ("PAY".equals(command)) {
            if (args.length == 0) {
                throw new IllegalArgumentException("PAY <billId1> <billId2> ...");
            }
            if (args.length == 1) {
                PaymentTransaction res = paymentService.paySingle(args[0], PaymentChannel.MANUAL);
                return "Paid bill " + res.billId() + " transaction=" + res.id();
            }
            PaymentService.BatchPaymentResult result = paymentService.payMultiple(Arrays.asList(args));
            return "Batch payment done. paid=" + result.paidBillIds() + " skipped=" + result.skippedBillIds();
        }
        if ("SCHEDULE".equals(command)) {
            requireArgs(args, 2, "SCHEDULE <billId> <dd/MM/yyyy>");
            ScheduledPayment sp = schedulingService.schedulePayment(args[0], DateUtil.parseDate(args[1]));
            return "Scheduled payment: " + sp.id();
        }
        if ("EXIT".equals(command)) {
            return "ZZZ";
        }
        throw new IllegalArgumentException("Unknown command: " + command);
    }

    private String formatBills(List<Bill> bills) {
        if (bills.isEmpty()) {
            return "No bills found.";
        }
        StringBuilder res = new StringBuilder();

        for (Bill bill : bills) {
            res.append(bill.id())
                    .append(", ")
                    .append(bill.type())
                    .append(", ")
                    .append(bill.amount())
                    .append(", ")
                    .append(DateUtil.format(bill.dueDate()))
                    .append(", ")
                    .append(bill.provider())
                    .append(", ")
                    .append(bill.status())
                    .append(System.lineSeparator());
        }

        return res.toString();
    }

    private void requireArgs(String[] args, int expected, String usage) {
        if (args.length != expected) {
            throw new IllegalArgumentException("Invalid arguments. Usage: " + usage);
        }
    }
}
