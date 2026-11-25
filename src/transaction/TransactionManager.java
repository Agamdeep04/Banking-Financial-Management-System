
package transaction;

import database.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import model.Customer;
import model.DepositTransaction;
import model.Transaction;
import model.WithdrawalTransaction;
import util.InputValidator;

public class TransactionManager {

    private final TransactionRepository repo = new TransactionRepository();

    private Customer loggedInCustomer = null;
    private boolean adminLoggedIn = false;

    // Update the current login session.
    public void setSession(Customer customer, boolean isAdmin) {
        this.loggedInCustomer = customer;
        this.adminLoggedIn = isAdmin;
    }

    public void recordDeposit(String account, BigDecimal amount) {

        Transaction tx = new DepositTransaction(
                0, account, amount, LocalDateTime.now().toString()
        );

        if (!repo.save(tx)) {
            System.out.println("Warning: Deposit transaction record failed.");
        }
    }

    public void recordWithdrawal(String account, BigDecimal amount) {

        Transaction tx = new WithdrawalTransaction(
                0, account, amount, LocalDateTime.now().toString()
        );

        if (!repo.save(tx)) {
            System.out.println("Warning: Withdrawal transaction record failed.");
        }
    }

    public void recordTransfer(String from, String to, BigDecimal amount) {

        String date = LocalDateTime.now().toString();

        Transaction debit = new Transaction(
                0, from, "TRANSFER", amount,
                "Transfer to " + to, date
        );

        Transaction credit = new Transaction(
                0, to, "TRANSFER", amount,
                "Transfer from " + from, date
        );

        repo.save(debit);
        repo.save(credit);
    }

    public List<Transaction> getAll() {
        return repo.loadAll();
    }

    // Check whether the logged-in customer owns this account.
    private boolean hasAccountAccess(String accountNumber) throws SQLException {

        if (adminLoggedIn) {
            return true;
        }

        if (loggedInCustomer == null) {
            return false;
        }

        String sql = "SELECT customer_id FROM BankAccounts WHERE account_number = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, accountNumber);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("customer_id") == loggedInCustomer.getId();
                }
            }
        }

        return false;
    }

    public void viewTransactionHistory(Scanner sc) {

        try {

            if (loggedInCustomer == null && !adminLoggedIn) {
                System.out.println("Please login first.");
                return;
            }

            String account = InputValidator.readNonEmptyString(
                    sc, "Account number: "
            );

            if (!hasAccountAccess(account)) {
                System.out.println(
                        "Access denied. You can only view your own account transactions."
                );
                return;
            }

            List<Transaction> transactions =
                    repo.findByAccountNumber(account);

            if (transactions.isEmpty()) {
                System.out.println("No transactions found.");
                return;
            }

            System.out.println("\n--- TRANSACTION HISTORY ---");

            for (Transaction tx : transactions) {
                System.out.println(tx);
            }

        } catch (InputValidator.CustomException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
