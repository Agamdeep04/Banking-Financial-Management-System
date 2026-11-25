package fixeddeposit;

import account.AccountManager;
import database.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Scanner;
import model.BankAccount;
import model.FixedDeposit;
import model.SavingsAccount;
import transaction.TransactionManager;
import util.InputValidator;

public class FixedDepositManager {

    private final AccountManager accountManager;

    public FixedDepositManager(AccountManager accountManager,
            TransactionManager transactionManager) {

        this.accountManager = accountManager;
    }

    public void manageFixedDeposit(Scanner sc) {

        try {
            System.out.println("1. Create Fixed Deposit");
            System.out.println("2. Calculate Maturity");

            int choice = InputValidator.readInt(sc, "Choice: ");

            switch (choice) {
                case 1 ->
                    createFD(sc);

                case 2 ->
                    calculateMaturity(sc);

                default ->
                    System.out.println("Invalid choice.");
            }

        } catch (InputValidator.CustomException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createFD(Scanner sc)
            throws InputValidator.CustomException {

        String number = InputValidator.readNonEmptyString(
                sc, "Savings account number: "
        );

        BankAccount account = accountManager.findByAccountNumber(number);

        if (!(account instanceof SavingsAccount)) {
            System.out.println("Valid savings account not found.");
            return;
        }

        if (!accountManager.isAccountAccessible(account)) {
            System.out.println("Access denied. You can only use your own account.");
            return;
        }

        BigDecimal principal = InputValidator.readBigDecimal(
                sc, "Principal amount: "
        );

        InputValidator.validatePositive(principal);

        double rate = InputValidator.readDouble(
                sc, "Annual interest rate: "
        );

        int months = InputValidator.readInt(
                sc, "Tenure in months: "
        );

        if (rate < 0 || months <= 0) {
            System.out.println("Invalid rate or tenure.");
            return;
        }

        String update = "UPDATE BankAccounts SET balance=balance-? "
                + "WHERE account_number=? AND status='ACTIVE' AND balance>=?";

        String insert = "INSERT INTO FixedDeposits "
                + "(account_number, principal, interest_rate, tenure_months, status) "
                + "VALUES (?, ?, ?, ?, 'ACTIVE')";

        String transaction = "INSERT INTO Transactions "
                + "(account_number, transaction_type, amount, description, transaction_date) "
                + "VALUES (?, 'WITHDRAW', ?, 'Fixed deposit funding', ?)";

        try (Connection con = DatabaseConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(update)) {

                    ps.setBigDecimal(1, principal);
                    ps.setString(2, number);
                    ps.setBigDecimal(3, principal);

                    if (ps.executeUpdate() == 0) {
                        throw new SQLException(
                                "Insufficient balance or inactive account."
                        );
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(insert)) {

                    ps.setString(1, number);
                    ps.setBigDecimal(2, principal);
                    ps.setDouble(3, rate);
                    ps.setInt(4, months);

                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(transaction)) {

                    ps.setString(1, number);
                    ps.setBigDecimal(2, principal);
                    ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

                    ps.executeUpdate();
                }

                con.commit();

                System.out.println("Fixed deposit created successfully.");

            } catch (SQLException e) {

                con.rollback();
                System.out.println("FD creation failed: " + e.getMessage());

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void calculateMaturity(Scanner sc)
            throws InputValidator.CustomException {

        String number = InputValidator.readNonEmptyString(
                sc, "Account number: "
        );

        BankAccount account = accountManager.findByAccountNumber(number);

        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        if (!accountManager.isAccountAccessible(account)) {
            System.out.println("Access denied. You can only view your own fixed deposits.");
            return;
        }

        String sql = "SELECT * FROM FixedDeposits "
                + "WHERE account_number=? AND status='ACTIVE'";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, number);

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                while (rs.next()) {

                    found = true;

                    FixedDeposit fd = new FixedDeposit(
                            rs.getInt("id"),
                            rs.getString("account_number"),
                            rs.getBigDecimal("principal"),
                            rs.getDouble("interest_rate"),
                            rs.getInt("tenure_months"),
                            rs.getString("status")
                    );

                    System.out.println(fd);
                    System.out.println(
                            "Maturity amount: "
                            + fd.calculateMaturityAmount()
                    );
                }

                if (!found) {
                    System.out.println("No active fixed deposits found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("FD search error: " + e.getMessage());
        }
    }
}
