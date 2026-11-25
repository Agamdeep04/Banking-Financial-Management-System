
package account;

import database.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.BankAccount;
import model.CurrentAccount;
import model.SavingsAccount;

public class AccountRepository {

    public boolean save(BankAccount acc) {

        String sql = "INSERT INTO BankAccounts " +
                "(account_number, customer_id, balance, account_type, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, acc.getAccountNumber());
            ps.setInt(2, acc.getCustomerId());
            ps.setBigDecimal(3, acc.getBalance());
            ps.setString(4, acc.getAccountType().toUpperCase());
            ps.setString(5, acc.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Account save error: " + e.getMessage());
            return false;
        }
    }

    public boolean update(BankAccount acc) {

        String sql = "UPDATE BankAccounts SET status=? WHERE account_number=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, acc.getStatus());
            ps.setString(2, acc.getAccountNumber());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Account update error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBalance(BankAccount acc) {

        String sql = "UPDATE BankAccounts SET balance=? WHERE account_number=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBigDecimal(1, acc.getBalance());
            ps.setString(2, acc.getAccountNumber());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Balance update error: " + e.getMessage());
            return false;
        }
    }

    public BankAccount findByAccountNumber(String number) {

        String sql = "SELECT * FROM BankAccounts WHERE account_number=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, number);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Account search error: " + e.getMessage());
        }

        return null;
    }

    public List<BankAccount> findByCustomerId(int customerId) {

        List<BankAccount> list = new ArrayList<>();

        String sql = "SELECT * FROM BankAccounts WHERE customer_id=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Account search error: " + e.getMessage());
        }

        return list;
    }

    public List<BankAccount> loadAll() {

        List<BankAccount> list = new ArrayList<>();

        String sql = "SELECT * FROM BankAccounts";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Account loading error: " + e.getMessage());
        }

        return list;
    }

    private BankAccount mapRow(ResultSet rs) throws SQLException {

        String number = rs.getString("account_number");
        int customerId = rs.getInt("customer_id");
        BigDecimal balance = rs.getBigDecimal("balance");
        String type = rs.getString("account_type");
        String status = rs.getString("status");

        if ("SAVINGS".equalsIgnoreCase(type)) {
            return new SavingsAccount(
                    number, customerId, balance, status, 4.0
            );
        }

        if ("CURRENT".equalsIgnoreCase(type)) {
            return new CurrentAccount(
                    number, customerId, balance, status, BigDecimal.ZERO
            );
        }

        throw new SQLException("Unknown account type: " + type);
    }
}


