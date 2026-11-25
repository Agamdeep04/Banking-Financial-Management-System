
package search;

import account.AccountManager;
import customer.CustomerManager;
import java.util.*;
import model.BankAccount;
import model.Customer;
import util.InputValidator;

public class SearchManager {
    private final CustomerManager customerManager;
    private final AccountManager accountManager;

    private Customer loggedInCustomer = null;
    private boolean adminLoggedIn = false;

    public SearchManager(CustomerManager customerManager, AccountManager accountManager) {
        this.customerManager = customerManager;
        this.accountManager = accountManager;
    }

    public void setSession(Customer customer, boolean isAdmin) {
        this.loggedInCustomer = customer;
        this.adminLoggedIn = isAdmin;
    }

    public void searchCustomers(Scanner sc) {
        if (!adminLoggedIn) {
            System.out.println("Access denied! Only admins can search customers.");
            return;
        }

        try {
            System.out.println("1. Search by Name");
            System.out.println("2. Search by Email");

            int choice = InputValidator.readInt(sc, "Choice: ");

            if (choice != 1 && choice != 2) {
                System.out.println("Invalid choice!");
                return;
            }

            String keyword = InputValidator.readNonEmptyString(sc, "Keyword: ");

            List<Customer> all = customerManager.getAll();

            if (choice == 1) {
                List<Customer> found = all.stream()
                        .filter(c -> c.getName().toLowerCase()
                                .contains(keyword.toLowerCase()))
                        .toList();

                found.forEach(System.out::println);
            } else {
                List<Customer> found = all.stream()
                        .filter(c -> c.getEmail().toLowerCase()
                                .contains(keyword.toLowerCase()))
                        .toList();

                found.forEach(System.out::println);
            }

            if (all.isEmpty()) {
                System.out.println("No customers found.");
            }

        } catch (InputValidator.CustomException e) {
            System.out.println(e.getMessage());
        }
    }

    public void searchAccounts(Scanner sc) {
        if (loggedInCustomer == null && !adminLoggedIn) {
            System.out.println("Please login first.");
            return;
        }

        try {
            System.out.println("1. Search by Customer ID");
            System.out.println("2. Search by Type");

            int choice = InputValidator.readInt(sc, "Choice: ");

            if (choice != 1 && choice != 2) {
                System.out.println("Invalid choice!");
                return;
            }

            List<BankAccount> all = accountManager.getAll();

            // Customers can only search their own accounts.
            if (!adminLoggedIn) {
                all = all.stream()
                        .filter(a -> a.getCustomerId() == loggedInCustomer.getId())
                        .toList();
            }

            if (choice == 1) {
                int custId;

                if (adminLoggedIn) {
                    custId = InputValidator.readInt(sc, "Customer ID: ");
                } else {
                    custId = loggedInCustomer.getId();
                    System.out.println("Showing accounts for your Customer ID: " + custId);
                }

                List<BankAccount> found = all.stream()
                        .filter(a -> a.getCustomerId() == custId)
                        .toList();

                if (found.isEmpty()) {
                    System.out.println("No accounts found.");
                } else {
                    found.forEach(System.out::println);
                }

            } else {
                String type = InputValidator.readNonEmptyString(sc, "Account Type: ")
                        .toUpperCase();

                List<BankAccount> found = all.stream()
                        .filter(a -> a.getAccountType().equalsIgnoreCase(type))
                        .toList();

                if (found.isEmpty()) {
                    System.out.println("No accounts found.");
                } else {
                    found.forEach(System.out::println);
                }
            }

        } catch (InputValidator.CustomException e) {
            System.out.println(e.getMessage());
        }
    }
}
