
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/BankingManagementSystem";

    private static final String USER = "root";

    // Change this to your actual MySQL password.
    private static final String PASS = "123456";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(
                    "MySQL JDBC Driver not found: " + e.getMessage()
            );
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void close(AutoCloseable... resources) {

        for (AutoCloseable resource : resources) {

            if (resource != null) {

                try {
                    resource.close();
                } catch (Exception e) {
                    System.out.println(
                            "Resource closing error: " + e.getMessage()
                    );
                }
            }
        }
    }
}

