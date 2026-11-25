
package transfer;

import account.AccountManager;
import database.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Scanner;
import model.BankAccount;
import util.InputValidator;

public class TransferManager {

    private final AccountManager accountManager;

    public TransferManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public void transfer(Scanner sc) {

        try {
            String from = InputValidator.readNonEmptyString(
                    sc, "Sender account: "
            );

            String to = InputValidator.readNonEmptyString(
                    sc, "Receiver account: "
            );

            BigDecimal amount = InputValidator.readBigDecimal(
                    sc, "Transfer amount: "
            );

            InputValidator.validatePositive(amount);

            if (from.equals(to)) {
                System.out.println("Cannot transfer to the same account.");
                return;
            }

            // Check whether the logged-in user owns the sender account.
            BankAccount senderAccount =
                    accountManager.findByAccountNumber(from);

            if (senderAccount == null) {
                System.out.println("Sender account not found.");
                return;
            }

            if (!accountManager.isAccountAccessible(senderAccount)) {
                System.out.println(
                        "Access denied. You can only transfer money from your own account."
                );
                return;
            }

            try (Connection con = DatabaseConnection.getConnection()) {

                con.setAutoCommit(false);

                try {
                    BigDecimal fromBalance;
                    BigDecimal toBalance;

                    String sql = "SELECT account_number, balance, status " +
                            "FROM BankAccounts WHERE account_number=? FOR UPDATE";

                    // Lock and check sender account.
                    try (PreparedStatement ps = con.prepareStatement(sql)) {

                        ps.setString(1, from);

                        try (ResultSet rs = ps.executeQuery()) {

                            if (!rs.next()) {
                                throw new SQLException(
                                        "Sender account not found."
                                );
                            }

                            if (!"ACTIVE".equalsIgnoreCase(
                                    rs.getString("status"))) {
                                throw new SQLException(
                                        "Sender account is not active."
                                );
                            }

                            fromBalance = rs.getBigDecimal("balance");
                        }
                    }

                    // Lock and check receiver account.
                    try (PreparedStatement ps = con.prepareStatement(sql)) {

                        ps.setString(1, to);

                        try (ResultSet rs = ps.executeQuery()) {

                            if (!rs.next()) {
                                throw new SQLException(
                                        "Receiver account not found."
                                );
                            }

                            if (!"ACTIVE".equalsIgnoreCase(
                                    rs.getString("status"))) {
                                throw new SQLException(
                                        "Receiver account is not active."
                                );
                            }

                            toBalance = rs.getBigDecimal("balance");
                        }
                    }

                    if (fromBalance.compareTo(amount) < 0) {
                        throw new SQLException("Insufficient balance.");
                    }

                    BigDecimal newFrom = fromBalance.subtract(amount);
                    BigDecimal newTo = toBalance.add(amount);

                    String update = "UPDATE BankAccounts SET balance=? " +
                            "WHERE account_number=?";

                    try (PreparedStatement ps = con.prepareStatement(update)) {

                        ps.setBigDecimal(1, newFrom);
                        ps.setString(2, from);
                        ps.executeUpdate();

                        ps.setBigDecimal(1, newTo);
                        ps.setString(2, to);
                        ps.executeUpdate();
                    }

                    // Record both sides of the transfer.
                    String insert = "INSERT INTO Transactions " +
                            "(account_number, transaction_type, amount, description, transaction_date) " +
                            "VALUES (?, 'TRANSFER', ?, ?, ?)";

                    try (PreparedStatement ps = con.prepareStatement(insert)) {

                        Timestamp date = Timestamp.valueOf(
                                LocalDateTime.now()
                        );

                        ps.setString(1, from);
                        ps.setBigDecimal(2, amount);
                        ps.setString(3, "Transfer to " + to);
                        ps.setTimestamp(4, date);
                        ps.executeUpdate();

                        ps.setString(1, to);
                        ps.setBigDecimal(2, amount);
                        ps.setString(3, "Transfer from " + from);
                        ps.setTimestamp(4, date);
                        ps.executeUpdate();
                    }

                    con.commit();

                    System.out.println("Transfer successful!");
                    System.out.println("Transferred: " + amount);

                } catch (Exception e) {

                    con.rollback();
                    System.out.println(
                            "Transfer failed: " + e.getMessage()
                    );

                } finally {
                    con.setAutoCommit(true);
                }
            }

        } catch (InputValidator.CustomException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
