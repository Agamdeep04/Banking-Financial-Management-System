
package loan;

import database.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Scanner;
import model.Customer;
import model.Loan;
import util.InputValidator;

public class LoanManager {

    private Customer loggedInCustomer = null;
    private boolean adminLoggedIn = false;

    public void setSession(Customer customer, boolean isAdmin) {
        this.loggedInCustomer = customer;
        this.adminLoggedIn = isAdmin;
    }

    private boolean hasAccountAccess(String accountNumber)
            throws SQLException {

        if (adminLoggedIn) {
            return true;
        }

        if (loggedInCustomer == null) {
            return false;
        }

        String sql = "SELECT customer_id FROM BankAccounts " +
                "WHERE account_number=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, accountNumber);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("customer_id") ==
                            loggedInCustomer.getId();
                }
            }
        }

        return false;
    }

    private boolean hasLoanAccess(int loanId) throws SQLException {

        if (adminLoggedIn) {
            return true;
        }

        if (loggedInCustomer == null) {
            return false;
        }

        String sql = "SELECT b.customer_id " +
                "FROM Loans l " +
                "JOIN BankAccounts b " +
                "ON l.account_number=b.account_number " +
                "WHERE l.id=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, loanId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("customer_id") ==
                            loggedInCustomer.getId();
                }
            }
        }

        return false;
    }

    public void manageLoan(Scanner sc) {

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
                        "Access denied. You can only create loans for your own account."
                );
                return;
            }

            BigDecimal principal = InputValidator.readBigDecimal(
                    sc, "Loan amount: "
            );

            InputValidator.validatePositive(principal);

            double rate = InputValidator.readDouble(
                    sc, "Annual interest rate: "
            );

            if (rate < 0) {
                System.out.println("Invalid interest rate.");
                return;
            }

            int months = InputValidator.readInt(
                    sc, "Tenure in months: "
            );

            if (months <= 0) {
                System.out.println("Tenure must be positive.");
                return;
            }

            String sql = "INSERT INTO Loans " +
                    "(account_number, principal, interest_rate, tenure_months, status) " +
                    "VALUES (?, ?, ?, ?, 'ACTIVE')";

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, account);
                ps.setBigDecimal(2, principal);
                ps.setDouble(3, rate);
                ps.setInt(4, months);

                if (ps.executeUpdate() > 0) {

                    Loan loan = new Loan(
                            0, account, principal, rate, months, "ACTIVE"
                    );

                    System.out.println("Loan created successfully.");
                    System.out.println("Monthly EMI: " + loan.calculateEMI());
                }
            }

        } catch (InputValidator.CustomException | SQLException e) {
            System.out.println("Loan error: " + e.getMessage());
        }
    }

    public void manageRepayment(Scanner sc) {

        try {
            if (loggedInCustomer == null && !adminLoggedIn) {
                System.out.println("Please login first.");
                return;
            }

            int loanId = InputValidator.readInt(sc, "Loan ID: ");

            if (!hasLoanAccess(loanId)) {
                System.out.println(
                        "Access denied. You can only repay your own loans."
                );
                return;
            }

            BigDecimal amount = InputValidator.readBigDecimal(
                    sc, "Repayment amount: "
            );

            InputValidator.validatePositive(amount);

            try (Connection con = DatabaseConnection.getConnection()) {

                con.setAutoCommit(false);

                try {
                    String sql = "SELECT principal, status FROM Loans " +
                            "WHERE id=? FOR UPDATE";

                    BigDecimal principal;
                    String status;

                    try (PreparedStatement ps = con.prepareStatement(sql)) {

                        ps.setInt(1, loanId);

                        try (ResultSet rs = ps.executeQuery()) {

                            if (!rs.next()) {
                                throw new SQLException("Loan not found.");
                            }

                            principal = rs.getBigDecimal("principal");
                            status = rs.getString("status");
                        }
                    }

                    if (!"ACTIVE".equalsIgnoreCase(status)) {
                        throw new SQLException("Loan is not active.");
                    }

                    String sumSql = "SELECT COALESCE(SUM(repayment_amount),0) " +
                            "FROM LoanRepayments WHERE loan_id=?";

                    BigDecimal paid;

                    try (PreparedStatement ps = con.prepareStatement(sumSql)) {

                        ps.setInt(1, loanId);

                        try (ResultSet rs = ps.executeQuery()) {
                            rs.next();
                            paid = rs.getBigDecimal(1);
                        }
                    }

                    BigDecimal remaining = principal.subtract(paid);

                    if (amount.compareTo(remaining) > 0) {
                        throw new SQLException(
                                "Repayment exceeds outstanding loan."
                        );
                    }

                    BigDecimal newRemaining = remaining.subtract(amount);

                    String insert = "INSERT INTO LoanRepayments " +
                            "(loan_id, repayment_amount, repayment_date, remaining_after) " +
                            "VALUES (?, ?, ?, ?)";

                    try (PreparedStatement ps = con.prepareStatement(insert)) {

                        ps.setInt(1, loanId);
                        ps.setBigDecimal(2, amount);
                        ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                        ps.setBigDecimal(4, newRemaining);

                        ps.executeUpdate();
                    }

                    if (newRemaining.compareTo(BigDecimal.ZERO) == 0) {

                        try (PreparedStatement ps = con.prepareStatement(
                                "UPDATE Loans SET status='PAID' WHERE id=?")) {

                            ps.setInt(1, loanId);
                            ps.executeUpdate();
                        }
                    }

                    con.commit();

                    System.out.println("Repayment successful.");
                    System.out.println("Remaining amount: " + newRemaining);

                } catch (Exception e) {

                    con.rollback();
                    System.out.println("Repayment failed: " + e.getMessage());

                } finally {
                    con.setAutoCommit(true);
                }
            }

        } catch (InputValidator.CustomException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
