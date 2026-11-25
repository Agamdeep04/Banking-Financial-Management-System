
package model;
import java.math.BigDecimal;

public class CurrentAccount extends BankAccount {

    private BigDecimal overdraftLimit;

    public CurrentAccount(String accountNumber, int customerId,
                          BigDecimal balance, String status,
                          BigDecimal overdraftLimit) {

        super(accountNumber, customerId, balance, status);

        if (overdraftLimit == null ||
                overdraftLimit.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException("Invalid overdraft limit.");
        }

        this.overdraftLimit = overdraftLimit;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    public void withdraw(BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException("Invalid withdrawal amount.");
        }

        if (!"ACTIVE".equalsIgnoreCase(getStatus())) {
            throw new IllegalStateException("Account is not active.");
        }

        BigDecimal available =
                getBalance().add(overdraftLimit);

        if (amount.compareTo(available) > 0) {
            throw new IllegalArgumentException("Overdraft limit exceeded.");
        }

        setBalance(getBalance().subtract(amount));
    }

    @Override
    public String getAccountType() {
        return "Current";
    }
}

