package model;

import java.math.BigDecimal;

public class WithdrawalTransaction extends Transaction {

    public WithdrawalTransaction(int id, String accountNumber, BigDecimal amount, String date) {
        super(id, accountNumber, "WITHDRAW", amount, "Cash withdrawal", date);
    }
}
