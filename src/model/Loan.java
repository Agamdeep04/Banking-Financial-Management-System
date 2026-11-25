
package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Loan {

    private int loanId;
    private String accountNumber;
    private BigDecimal principal;
    private double interestRate;
    private int tenureMonths;
    private String status;
    private BigDecimal remainingAmount;

    public Loan(int loanId, String accountNumber,
                BigDecimal principal, double interestRate,
                int tenureMonths, String status) {

        if (principal == null ||
                principal.compareTo(BigDecimal.ZERO) <= 0 ||
                !Double.isFinite(interestRate) ||
                interestRate < 0 ||
                tenureMonths <= 0) {

            throw new IllegalArgumentException("Invalid loan details.");
        }

        this.loanId = loanId;
        this.accountNumber = accountNumber;
        this.principal = principal;
        this.interestRate = interestRate;
        this.tenureMonths = tenureMonths;
        this.status = status;
        this.remainingAmount = principal;
    }

    public int getLoanId() {
        return loanId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRemainingAmount(BigDecimal amount) {
        this.remainingAmount = amount;
    }

    public BigDecimal calculateEMI() {

        double monthlyRate = interestRate / 1200.0;

        if (monthlyRate == 0) {

            return principal.divide(
                    BigDecimal.valueOf(tenureMonths),
                    2,
                    RoundingMode.HALF_UP
            );
        }

        double power = Math.pow(1 + monthlyRate, tenureMonths);

        double emi = principal.doubleValue() *
                monthlyRate * power / (power - 1);

        return BigDecimal.valueOf(emi).setScale(
                2, RoundingMode.HALF_UP
        );
    }

    @Override
    public String toString() {

        return "Loan ID: " + loanId +
                "\nAccount: " + accountNumber +
                "\nPrincipal: " + principal +
                "\nInterest: " + interestRate +
                "%\nTenure: " + tenureMonths +
                " months\nRemaining: " + remainingAmount +
                "\nStatus: " + status;
    }
}

