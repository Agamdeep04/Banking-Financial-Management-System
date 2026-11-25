package model;
import java.math.BigDecimal;

public class DepositTransaction extends Transaction {
    public DepositTransaction(int id, String accountNumber, BigDecimal amount, String date) {
        super(id, accountNumber, "DEPOSIT", amount, "Cash deposit", date);
    }
}
