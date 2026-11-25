
package account;

import customer.CustomerRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import model.BankAccount;
import model.CurrentAccount;
import model.Customer;
import model.SavingsAccount;
import transaction.TransactionManager;
import util.InputValidator;

public class AccountManager {

    private final AccountRepository repo = new AccountRepository();
    private final CustomerRepository customerRepo = new CustomerRepository();
    private final TransactionManager transactionManager;

    // Current logged-in session
    private Customer loggedInCustomer = null;
    private boolean adminLoggedIn = false;

    public AccountManager() {
        this(new TransactionManager());
    }

    public AccountManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    // Set the current user session
    public void setSession(Customer customer, boolean isAdmin) {
        this.loggedInCustomer = customer;
        this.adminLoggedIn = isAdmin;
    }

    // Check whether the logged-in user can access an account
    public boolean isAccountAccessible(BankAccount account) {

        if (account == null) {
            return false;
        }

        if (adminLoggedIn) {
            return true;
        }

        return loggedInCustomer != null &&
                account.getCustomerId() == loggedInCustomer.getId();
    }

    public void createAccount(Scanner sc) {

        try {
            int customerId;

            if (adminLoggedIn) {

                customerId = InputValidator.readInt(
                        sc, "Customer ID: "
                );

            } else if (loggedInCustomer != null) {

                customerId = loggedInCustomer.getId();

                System.out.println(
                        "Creating account for Customer ID: " + customerId
                );

            } else {
                System.out.println("Please login first.");
                return;
            }

            Customer customer = customerRepo.findById(customerId);

            if (customer == null) {
                System.out.println("Customer does not exist.");
                return;
            }

            String type = InputValidator.readNonEmptyString(
                    sc, "Account type (SAVINGS/CURRENT): "
            ).toUpperCase();

            BigDecimal balance = InputValidator.readBigDecimal(
                    sc, "Initial deposit: "
            );

            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                System.out.println("Invalid initial deposit.");
                return;
            }

            String number = "ACC" + System.currentTimeMillis();

            BankAccount account;

            if (type.equals("SAVINGS")) {

                account = new SavingsAccount(
                        number, customerId, balance, "ACTIVE", 4.0
                );

            } else if (type.equals("CURRENT")) {

                account = new CurrentAccount(
                        number, customerId, balance,
                        "ACTIVE", BigDecimal.ZERO
                );

            } else {
                System.out.println("Invalid account type.");
                return;
            }

            if (repo.save(account)) {

                System.out.println("Account created successfully!");
                System.out.println(account);

                if (balance.compareTo(BigDecimal.ZERO) > 0) {
                    transactionManager.recordDeposit(number, balance);
                }

            } else {
                System.out.println("Account creation failed.");
            }

        } catch (InputValidator.CustomException e) {
            System.out.println(e.getMessage());
        }
    }

    public BankAccount findByAccountNumber(String number) {
        return repo.findByAccountNumber(number);
    }

    public List<BankAccount> getAll() {
        return repo.loadAll();
    }

    public boolean updateBalance(BankAccount account) {
        return repo.updateBalance(account);
    }

    public void viewAccountDetails(Scanner sc) {

        try {
            String number = InputValidator.readNonEmptyString(
                    sc, "Account number: "
            );

            BankAccount account = findByAccountNumber(number);

            if (account == null) {
                System.out.println("Account not found.");
                return;
            }

            if (!isAccountAccessible(account)) {
                System.out.println("Access denied. This account belongs to another customer.");
                return;
            }

            System.out.println(account);

        } catch (InputValidator.CustomException e) {
            System.out.println(e.getMessage());
        }
    }

    public void checkBalance(Scanner sc) {

        try {
            String number = InputValidator.readNonEmptyString(
                    sc, "Account number: "
            );

            BankAccount account = findByAccountNumber(number);

            if (account == null) {
                System.out.println("Account not found.");
                return;
            }

            if (!isAccountAccessible(account)) {
                System.out.println("Access denied. This account belongs to another customer.");
                return;
            }

            System.out.println("Available balance: " +
                    account.getBalance());

        } catch (InputValidator.CustomException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deposit(Scanner sc) {

        try {
            String number = InputValidator.readNonEmptyString(
                    sc, "Account number: "
            );

            BankAccount account = findByAccountNumber(number);

            if (account == null) {
                System.out.println("Account not found.");
                return;
            }

            if (!isAccountAccessible(account)) {
                System.out.println("Access denied. This account belongs to another customer.");
                return;
            }

            BigDecimal amount = InputValidator.readBigDecimal(
                    sc, "Deposit amount: "
            );

            InputValidator.validatePositive(amount);

            account.deposit(amount);

            if (repo.updateBalance(account)) {
                transactionManager.recordDeposit(number, amount);
                System.out.println("Deposit successful.");
            } else {
                System.out.println("Database update failed.");
            }

        } catch (InputValidator.CustomException |
                 IllegalArgumentException |
                 IllegalStateException e) {

            System.out.println(e.getMessage());
        }
    }

    public void withdraw(Scanner sc) {

        try {
            String number = InputValidator.readNonEmptyString(
                    sc, "Account number: "
            );

            BankAccount account = findByAccountNumber(number);

            if (account == null) {
                System.out.println("Account not found.");
                return;
            }

            if (!isAccountAccessible(account)) {
                System.out.println("Access denied. This account belongs to another customer.");
                return;
            }

            BigDecimal amount = InputValidator.readBigDecimal(
                    sc, "Withdrawal amount: "
            );

            InputValidator.validatePositive(amount);

            account.withdraw(amount);

            if (repo.updateBalance(account)) {
                transactionManager.recordWithdrawal(number, amount);
                System.out.println("Withdrawal successful.");
            } else {
                System.out.println("Database update failed.");
            }

        } catch (InputValidator.CustomException |
                 IllegalArgumentException |
                 IllegalStateException e) {

            System.out.println(e.getMessage());
        }
    }

    public void closeAccount(Scanner sc) {

        try {
            String number = InputValidator.readNonEmptyString(
                    sc, "Account number: "
            );

            BankAccount account = findByAccountNumber(number);

            if (account == null) {
                System.out.println("Account not found.");
                return;
            }

            if (!isAccountAccessible(account)) {
                System.out.println("Access denied. This account belongs to another customer.");
                return;
            }

            if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                System.out.println("Withdraw or transfer remaining balance first.");
                return;
            }

            account.setStatus("CLOSED");

            if (repo.update(account)) {
                System.out.println("Account closed successfully.");
            } else {
                System.out.println("Account closure failed.");
            }

        } catch (InputValidator.CustomException e) {
            System.out.println(e.getMessage());
        }
    }

    public void calculateInterest(Scanner sc) {

        try {
            String number = InputValidator.readNonEmptyString(
                    sc, "Savings account number: "
            );

            BankAccount account = findByAccountNumber(number);

            if (account == null) {
                System.out.println("Account not found.");
                return;
            }

            if (!isAccountAccessible(account)) {
                System.out.println("Access denied. This account belongs to another customer.");
                return;
            }

            if (account instanceof SavingsAccount) {

                SavingsAccount savings = (SavingsAccount) account;

                System.out.println("Interest amount: " +
                        savings.calculateInterest());

            } else {
                System.out.println("Savings account not found.");
            }

        } catch (InputValidator.CustomException e) {
            System.out.println(e.getMessage());
        }
    }
}
