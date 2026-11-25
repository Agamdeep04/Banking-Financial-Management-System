package model;

import java.math.BigDecimal;

public class LoanRepayment {
    private int repaymentId;
    private int loanId;
    private BigDecimal amountPaid;
    private String repaymentDate;
    private BigDecimal remainingAfter;

    public LoanRepayment(int repaymentId, int loanId, BigDecimal amountPaid, String repaymentDate, BigDecimal remainingAfter) {
        this.repaymentId = repaymentId;
        this.loanId = loanId;
        this.amountPaid = amountPaid;
        this.repaymentDate = repaymentDate;
        this.remainingAfter = remainingAfter;
    }

    public int getRepaymentId() { return repaymentId; }
    public int getLoanId() { return loanId; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public String getRepaymentDate() { return repaymentDate; }
    public BigDecimal getRemainingAfter() { return remainingAfter; }

    @Override
    public String toString() {
        return "Repayment#" + repaymentId + " Loan:" + loanId + " Paid:" + amountPaid
                + " Date:" + repaymentDate + " Remaining:" + remainingAfter;
    }
}
