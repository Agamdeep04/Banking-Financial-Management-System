
package customer;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Admin;
import model.Customer;

public class CustomerRepository {

    // Generate next customer ID
    public int getNextId() {

        String sql = "SELECT COALESCE(MAX(id), 0) + 1 AS next_id FROM Customers";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("next_id");
            }

        } catch (SQLException e) {
            System.out.println("ID generation error: " + e.getMessage());
        }

        return 1;
    }

    // Find customer using email
    public Customer findByEmail(String email) {

        String sql = "SELECT * FROM Customers WHERE email = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Find customer error: " + e.getMessage());
        }

        return null;
    }

    // Save new customer
    public boolean save(Customer customer) {

        String sql = "INSERT INTO Customers "
                + "(id, customer_code, name, email, phone, password_hash, registered_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customer.getId());
            ps.setString(2, customer.getCustomerCode());
            ps.setString(3, customer.getName());
            ps.setString(4, customer.getEmail());
            ps.setString(5, customer.getPhone());
            ps.setString(6, customer.getPasswordHash());

            String date = customer.getRegisteredDate();

            if (date == null || date.trim().isEmpty()) {
                ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            } else {
                ps.setTimestamp(7, Timestamp.valueOf(date));
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Save customer error: " + e.getMessage());
        }

        return false;
    }

    // Customer login
    public Customer findByEmailAndPasswordHash(
            String email, String passwordHash) {

        String sql = "SELECT * FROM Customers WHERE email = ? AND password_hash = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Customer login error: " + e.getMessage());
        }

        return null;
    }

    // Admin login
    public Admin findAdminByEmailAndPasswordHash(
            String email, String passwordHash) {

        String sql = "SELECT * FROM Admins WHERE email = ? AND password_hash = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    return new Admin(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            "",
                            rs.getString("admin_code"),
                            rs.getString("password_hash")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Admin login error: " + e.getMessage());
        }

        return null;
    }

    // Find customer using ID
    public Customer findById(int id) {

        String sql = "SELECT * FROM Customers WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Find customer by ID error: " + e.getMessage());
        }

        return null;
    }

    // Update customer details
    public boolean update(Customer customer) {

        String sql = "UPDATE Customers SET name = ?, email = ?, "
                + "phone = ?, customer_code = ?, password_hash = ? WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getCustomerCode());
            ps.setString(5, customer.getPasswordHash());
            ps.setInt(6, customer.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Update customer error: " + e.getMessage());
        }

        return false;
    }

    // Load all customers
    public List<Customer> loadAll() {

        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT * FROM Customers ORDER BY id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                customers.add(mapCustomer(rs));
            }

        } catch (SQLException e) {
            System.out.println("Load customers error: " + e.getMessage());
        }

        return customers;
    }

    // Convert database record into Customer object
    private Customer mapCustomer(ResultSet rs) throws SQLException {

        Customer customer = new Customer(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("customer_code"),
                rs.getString("password_hash")
        );

        Timestamp registeredDate = rs.getTimestamp("registered_date");

        if (registeredDate != null) {
            customer.setRegisteredDate(registeredDate.toString());
        }

        return customer;
    }
}

