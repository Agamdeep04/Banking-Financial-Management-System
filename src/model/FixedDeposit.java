package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FixedDeposit {

    private int fdId;
    private String accountNumber;
    private BigDecimal principal;
    private double interestRate;
    private int tenureMonths;
    private String status;

    public FixedDeposit(int fdId, String accountNumber,
            BigDecimal principal, double interestRate,
            int tenureMonths, String status) {

        this.fdId = fdId;
        this.accountNumber = accountNumber;
        this.principal = principal;
        this.interestRate = interestRate;
        this.tenureMonths = tenureMonths;
        this.status = status;
    }

    public int getFdId() {
        return fdId;
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

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal calculateMaturityAmount() {

        BigDecimal rate = BigDecimal.valueOf(interestRate);

        BigDecimal interest = principal
                .multiply(rate)
                .multiply(BigDecimal.valueOf(tenureMonths))
                .divide(
                        BigDecimal.valueOf(1200),
                        2,
                        RoundingMode.HALF_UP
                );

        return principal.add(interest);
    }

    @Override
    public String toString() {

        return "FD ID: " + fdId
                + "\nAccount: " + accountNumber
                + "\nPrincipal: " + principal
                + "\nRate: " + interestRate
                + "%\nTenure: " + tenureMonths
                + " months\nStatus: " + status;
    }
}

