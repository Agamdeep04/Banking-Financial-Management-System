
package menu;

import account.AccountManager;
import csv.CSVManager;
import customer.CustomerManager;
import fixeddeposit.FixedDepositManager;
import java.util.Scanner;
import loan.LoanManager;
import model.Admin;
import model.Customer;
import report.ReportManager;
import search.SearchManager;
import sort.SortManager;
import threads.ThreadManager;
import transaction.TransactionManager;
import transfer.TransferManager;
import util.InputValidator;

public class MenuManager {

    private final Scanner scanner = new Scanner(System.in);

    private Customer loggedInCustomer = null;
    private Admin loggedInAdmin = null;

    private final CustomerManager customerManager;
    private final TransactionManager transactionManager;
    private final AccountManager accountManager;
    private final TransferManager transferManager;
    private final FixedDepositManager fixedDepositManager;
    private final LoanManager loanManager;
    private final ReportManager reportManager;
    private final SearchManager searchManager;
    private final CSVManager csvManager;
    private final SortManager sortManager;
    private final ThreadManager threadManager;

    public MenuManager() {

        transactionManager = new TransactionManager();
        customerManager = new CustomerManager();
        accountManager = new AccountManager(transactionManager);
        transferManager = new TransferManager(accountManager);

        fixedDepositManager = new FixedDepositManager(
                accountManager, transactionManager
        );

        loanManager = new LoanManager();

        reportManager = new ReportManager(
                accountManager, transactionManager
        );

        searchManager = new SearchManager(
                customerManager, accountManager
        );

        csvManager = new CSVManager(
                customerManager, transactionManager
        );

        sortManager = new SortManager();
        threadManager = new ThreadManager();
    }

    public void start() {

        boolean running = true;

        while (running) {

            printMenu();

            try {
                int choice = InputValidator.readInt(
                        scanner, "Enter choice: "
                );

                running = handleChoice(choice);

                if (running) {
                    System.out.println();
                    System.out.println("----------------------------------------");
                    System.out.println("Operation completed.");
                    System.out.println("Press ENTER to return to the main menu...");
                    System.out.println("----------------------------------------");

                    scanner.nextLine();

                    System.out.println("Returning to main menu...");
                }

            } catch (InputValidator.CustomException e) {

                System.out.println(e.getMessage());

                System.out.println("Press ENTER to continue...");
                scanner.nextLine();
            }
        }
    }

    private void printMenu() {

        System.out.println("\n===== BANKING AND FINANCIAL MANAGEMENT =====");

        System.out.println("1. Customer Registration");
        System.out.println("2. Customer Login");
        System.out.println("3. Admin Login");
        System.out.println("4. Create Bank Account");
        System.out.println("5. View Account Details");
        System.out.println("6. Deposit Money");
        System.out.println("7. Withdraw Money");
        System.out.println("8. Transfer Money");
        System.out.println("9. Check Balance");
        System.out.println("10. Transaction History");
        System.out.println("11. Update Customer Details");
        System.out.println("12. Search Customers");
        System.out.println("13. Search Accounts");
        System.out.println("14. Close Account");
        System.out.println("15. Fixed Deposit Management");
        System.out.println("16. Calculate Interest");
        System.out.println("17. Loan Management");
        System.out.println("18. Loan Repayment");
        System.out.println("19. Financial Reports");
        System.out.println("20. Sort and Filter");
        System.out.println("21. Import CSV");
        System.out.println("22. Export CSV");
        System.out.println("23. Multithreading");
        System.out.println("24. Logout");
        System.out.println("25. Exit");
    }

    private boolean handleChoice(int choice) {

        accountManager.setSession(
                loggedInCustomer, loggedInAdmin != null
        );

        transactionManager.setSession(
                loggedInCustomer, loggedInAdmin != null
        );

        loanManager.setSession(
                loggedInCustomer, loggedInAdmin != null
        );

        customerManager.setSession(
                loggedInCustomer, loggedInAdmin != null
        );

        searchManager.setSession(
                loggedInCustomer, loggedInAdmin != null
        );

        reportManager.setSession(
                loggedInCustomer, loggedInAdmin != null
        );

        csvManager.setSession(
                loggedInCustomer, loggedInAdmin != null
        );

        // Restrict access when nobody is logged in
        if (loggedInCustomer == null && loggedInAdmin == null) {

            if (choice != 1 && choice != 2 &&
                    choice != 3 && choice != 25) {

                System.out.println("Please login first.");
                return true;
            }
        }

        // Restrict admin-only operations for customers
        if (loggedInCustomer != null) {

            if (choice == 12 || choice == 13 || choice == 14 ||
                    choice == 19 || choice == 20 || choice == 21 ||
                    choice == 22 || choice == 23) {

                System.out.println("Access denied. Admin login required.");
                return true;
            }
        }

        switch (choice) {

            case 1 -> customerManager.registerCustomer(scanner);

            case 2 -> {
                loggedInCustomer = customerManager.customerLogin(scanner);

                if (loggedInCustomer != null) {
                    loggedInAdmin = null;
                    System.out.println("Customer session started.");
                }
            }

            case 3 -> {
                loggedInAdmin = customerManager.adminLogin(scanner);

                if (loggedInAdmin != null) {
                    loggedInCustomer = null;
                    System.out.println("Admin session started.");
                }
            }

            case 4 -> accountManager.createAccount(scanner);

            case 5 -> accountManager.viewAccountDetails(scanner);

            case 6 -> accountManager.deposit(scanner);

            case 7 -> accountManager.withdraw(scanner);

            case 8 -> transferManager.transfer(scanner);

            case 9 -> accountManager.checkBalance(scanner);

            case 10 -> transactionManager.viewTransactionHistory(scanner);

            case 11 -> customerManager.updateCustomerDetails(scanner);

            case 12 -> searchManager.searchCustomers(scanner);

            case 13 -> searchManager.searchAccounts(scanner);

            case 14 -> accountManager.closeAccount(scanner);

            case 15 -> fixedDepositManager.manageFixedDeposit(scanner);

            case 16 -> accountManager.calculateInterest(scanner);

            case 17 -> loanManager.manageLoan(scanner);

            case 18 -> loanManager.manageRepayment(scanner);

            case 19 -> reportManager.generateReports(scanner);

            case 20 -> sortManager.sortAndFilter(scanner);

            case 21 -> csvManager.importCustomers(scanner);

            case 22 -> csvManager.exportTransactions(scanner);

            case 23 -> threadManager.demonstrateMultithreading();

            case 24 -> {

                if (loggedInCustomer != null || loggedInAdmin != null) {

                    loggedInCustomer = null;
                    loggedInAdmin = null;

                    accountManager.setSession(null, false);
                    transactionManager.setSession(null, false);
                    loanManager.setSession(null, false);
                    customerManager.setSession(null, false);
                    searchManager.setSession(null, false);
                    reportManager.setSession(null, false);
                    csvManager.setSession(null, false);

                    System.out.println("You have logged out successfully.");

                } else {
                    System.out.println("You are not logged in.");
                }
            }

            case 25 -> {
                System.out.println("Thank you for using our banking system.");
                return false;
            }

            default -> System.out.println("Invalid choice.");
        }

        return true;
    }
}
