
package report;

import account.AccountManager;
import java.util.*;
import model.BankAccount;
import model.Customer;
import model.Transaction;
import transaction.TransactionManager;
import util.InputValidator;

public class ReportManager {

    private final AccountManager accountManager;
    private final TransactionManager transactionManager;

    private boolean adminLoggedIn = false;

    public ReportManager(AccountManager accountManager,
                         TransactionManager transactionManager) {

        this.accountManager = accountManager;
        this.transactionManager = transactionManager;
    }

    public void setSession(Customer customer, boolean isAdmin) {
        this.adminLoggedIn = isAdmin;
    }

    public void generateReports(Scanner sc) {

        if (!adminLoggedIn) {
            System.out.println("Access denied! Only admins can generate financial reports.");
            return;
        }

        try {
            System.out.println("1. Account Summary Report");
            System.out.println("2. Transaction History Report");

            int choice = InputValidator.readInt(sc, "Choice: ");

            switch (choice) {

    case 1 -> {

        List<BankAccount> accs = accountManager.getAll();

        System.out.println("\n=== ACCOUNT SUMMARY ===");

        if (accs.isEmpty()) {
            System.out.println("No accounts found.");
        } else {
            accs.forEach(System.out::println);
        }
    }

    case 2 -> {

        List<Transaction> txns = transactionManager.getAll();

        System.out.println("\n=== TRANSACTION HISTORY ===");

        if (txns.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            txns.forEach(System.out::println);
        }
    }

    default -> System.out.println("Invalid choice.");
}

        } catch (InputValidator.CustomException e) {
            System.out.println(e.getMessage());
        }
    }
}
