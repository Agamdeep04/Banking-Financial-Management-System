
package model;

import java.math.BigDecimal;

public class TransferTransaction extends Transaction {

    private final String toAccountNumber;

    public TransferTransaction(int id, String fromAccount,
                               String toAccount, BigDecimal amount,
                               String date) {

        super(id, fromAccount, "TRANSFER", amount,
                "Transfer to " + toAccount, date);

        this.toAccountNumber = toAccount;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }
}
