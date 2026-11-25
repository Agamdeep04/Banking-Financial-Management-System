
package csv;

import customer.CustomerManager;
import java.io.*;
import java.util.*;
import model.Customer;
import model.Transaction;
import transaction.TransactionManager;
import util.InputValidator;

public class CSVManager {

    private final CustomerManager customerManager;
    private final TransactionManager transactionManager;

    private boolean adminLoggedIn = false;

    public CSVManager(CustomerManager customerManager,
                      TransactionManager transactionManager) {

        this.customerManager = customerManager;
        this.transactionManager = transactionManager;
    }

    public void setSession(Customer customer, boolean isAdmin) {
        this.adminLoggedIn = isAdmin;
    }

    public void importCustomers(Scanner sc) {

        if (!adminLoggedIn) {
            System.out.println("Access denied! Admin login required.");
            return;
        }

        try {
            String path = InputValidator.readNonEmptyString(
                    sc, "CSV file path: "
            );

            int count = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(path))) {

                String line;
                boolean header = true;

                while ((line = br.readLine()) != null) {

                    if (header) {
                        header = false;
                        continue;
                    }

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    List<String> parts = parseLine(line);

                    if (parts.size() != 4) {
                        System.out.println("Invalid row skipped.");
                        continue;
                    }

                    String name = parts.get(0).trim();
                    String email = parts.get(1).trim();
                    String phone = parts.get(2).trim();
                    String password = parts.get(3).trim();

                    if (name.isEmpty() ||
                            !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") ||
                            !phone.matches("\\d{10}") ||
                            password.isEmpty()) {

                        System.out.println("Invalid customer data skipped.");
                        continue;
                    }

                    int id = customerManager.nextId();

                    Customer customer = new Customer(
                            id,
                            name,
                            email,
                            phone,
                            "C" + id,
                            Customer.hashPassword(password)
                    );

                    customer.setRegisteredDate(
                            java.time.LocalDateTime.now().toString().replace("T", " ")
                    );

                    customerManager.addCustomer(customer);

                    count++;
                }
            }

            System.out.println("Import completed. Processed: " + count);

        } catch (IOException | InputValidator.CustomException e) {
            System.out.println("Import error: " + e.getMessage());
        }
    }

    private List<String> parseLine(String line) {

        List<String> result = new ArrayList<>();
        StringBuilder value = new StringBuilder();

        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {

            char ch = line.charAt(i);

            if (ch == '"') {

                if (quoted && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {

                    value.append('"');
                    i++;

                } else {
                    quoted = !quoted;
                }

            } else if (ch == ',' && !quoted) {

                result.add(value.toString());
                value.setLength(0);

            } else {
                value.append(ch);
            }
        }

        result.add(value.toString());

        return result;
    }

    public void exportTransactions(Scanner sc) {

        if (!adminLoggedIn) {
            System.out.println("Access denied! Admin login required.");
            return;
        }

        try {
            String path = InputValidator.readNonEmptyString(
                    sc, "Output CSV path: "
            );

            try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {

                pw.println("AccountNumber,Type,Amount,Description,Date");

                for (Transaction tx : transactionManager.getAll()) {

                    pw.println(
                            escape(tx.getAccountNumber()) + "," +
                            escape(tx.getType()) + "," +
                            tx.getAmount() + "," +
                            escape(tx.getDescription()) + "," +
                            escape(tx.getTransactionDate())
                    );
                }

                if (pw.checkError()) {
                    System.out.println("Error writing CSV file.");
                } else {
                    System.out.println("Transactions exported successfully.");
                }
            }

        } catch (IOException | InputValidator.CustomException e) {
            System.out.println("Export error: " + e.getMessage());
        }
    }

    private String escape(String value) {

        if (value == null) {
            return "";
        }

        if (value.contains(",") || value.contains("\"")
                || value.contains("\n")) {

            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }
}
