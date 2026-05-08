# Bill Manage Service By Tran Duc Tung

## Something I clarify and figure out, or just to define it clearly because the assignment did not clarify them.

- Date format is  `dd/MM/yyyy`.
- Bill can be paid one time.
- Bills that are paid cannot be updated,deleted,scheduled.
- Scheduling allows only one active schedule for a bill id on the same date.
- Scheduled jobs will be in doing in while loop.

## Some rules

- Requested bill IDs are de-duplicated.
- Unpaid target bills are sorted by earliest due date, then by bill ID.
- If balance is insufficient for a bill, that bill is skipped and marked failed in payment history.
- Processing continues for remaining bills (so smaller later bills may still be paid).


## Build

```bash
mvn clean test
mvn clean package
```

## Run

```bash
java -jar target/bill-manage-service-1.0.0.jar
```

## Commands

- `CASH_IN <amount>`
- `CREATE_BILL <type> <amount> <dueDate> <provider>`
- `UPDATE_BILL <id> <type> <amount> <dueDate> <provider>`
- `DELETE_BILL <id>`
- `LIST_BILL`
- `SEARCH_BILL_BY_PROVIDER <provider>`
- `PAY <billId1> <billId2> ...`
- `SCHEDULE <billId> <dd/MM/yyyy>`
- `EXIT`
