
package transaction;

import java.sql.*;

import model.Transaction;
import database.DatabaseConnection;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {

    public boolean save(Transaction tx) {

        String sql = "INSERT INTO Transactions " +
                "(account_number, transaction_type, amount, description, transaction_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tx.getAccountNumber());
            ps.setString(2, tx.getType());
            ps.setBigDecimal(3, tx.getAmount());
            ps.setString(4, tx.getDescription());
            ps.setTimestamp(5, Timestamp.valueOf(
                    java.time.LocalDateTime.parse(tx.getTransactionDate())
            ));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Transaction save error: " + e.getMessage());
            return false;
        }
    }

    public List<Transaction> findByAccountNumber(String number) {

        List<Transaction> list = new ArrayList<>();

        String sql = "SELECT * FROM Transactions WHERE account_number=? ORDER BY id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, number);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Transaction search error: " + e.getMessage());
        }

        return list;
    }

    public List<Transaction> loadAll() {

        List<Transaction> list = new ArrayList<>();

        String sql = "SELECT * FROM Transactions ORDER BY id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Transaction loading error: " + e.getMessage());
        }

        return list;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {

        return new Transaction(
                rs.getInt("id"),
                rs.getString("account_number"),
                rs.getString("transaction_type"),
                rs.getBigDecimal("amount"),
                rs.getString("description"),
                rs.getTimestamp("transaction_date").toLocalDateTime().toString()
        );
    }
}

