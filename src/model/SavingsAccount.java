
package model;

import java.math.BigDecimal;

public class SavingsAccount extends BankAccount {

    private double interestRate;

    public SavingsAccount(String accountNumber, int customerId,
                          BigDecimal balance, String status,
                          double interestRate) {

        super(accountNumber, customerId, balance, status);

        if (!Double.isFinite(interestRate) || interestRate < 0) {
            throw new IllegalArgumentException("Invalid interest rate.");
        }

        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public BigDecimal calculateInterest() {

        return getBalance().multiply(
                BigDecimal.valueOf(interestRate)
                        .divide(BigDecimal.valueOf(100))
        );
    }

    @Override
    public String getAccountType() {
        return "Savings";
    }
}
