
package model;

import java.math.BigDecimal;

public abstract class BankAccount {

    private String accountNumber;
    private int customerId;
    private BigDecimal balance;
    private String status;

    public BankAccount(String accountNumber, int customerId,
                       BigDecimal balance, String status) {

        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid balance.");
        }

        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.balance = balance;
        this.status = status;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getCustomerId() {
        return customerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBalance(BigDecimal balance) {

        if (balance == null) {
            throw new IllegalArgumentException("Balance cannot be null.");
        }

        this.balance = balance;
    }

    public void deposit(BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException("Invalid deposit amount.");
        }

        if (!"ACTIVE".equalsIgnoreCase(status)) {
            throw new IllegalStateException("Account is not active.");
        }

        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException("Invalid withdrawal amount.");
        }

        if (!"ACTIVE".equalsIgnoreCase(status)) {
            throw new IllegalStateException("Account is not active.");
        }

        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        balance = balance.subtract(amount);
    }

    public abstract String getAccountType();

    @Override
    public String toString() {

        return getAccountType() +
                " [Account: " + accountNumber +
                ", Customer: " + customerId +
                ", Balance: " + balance +
                ", Status: " + status + "]";
    }
}
