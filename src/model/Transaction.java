
package model;

import java.math.BigDecimal;

public class Transaction {

    private int transactionId;
    private String accountNumber;
    private String type;
    private BigDecimal amount;
    private String description;
    private String transactionDate;

    public Transaction(int transactionId, String accountNumber,
                       String type, BigDecimal amount,
                       String description, String transactionDate) {

        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    @Override
    public String toString() {

        return "Transaction ID: " + transactionId +
                "\nAccount: " + accountNumber +
                "\nType: " + type +
                "\nAmount: ₹" + amount.toPlainString() +
                "\nDescription: " + description +
                "\nDate: " + transactionDate;
    }
}

