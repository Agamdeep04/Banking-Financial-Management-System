
package customer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import model.Admin;
import model.Customer;
import util.InputValidator;

public class CustomerManager {

    private final CustomerRepository repo = new CustomerRepository();

    private Customer loggedInCustomer = null;
    private boolean adminLoggedIn = false;

    public void setSession(Customer customer, boolean isAdmin) {
        this.loggedInCustomer = customer;
        this.adminLoggedIn = isAdmin;
    }

    public void registerCustomer(Scanner sc) {

        try {
            String name = InputValidator.readNonEmptyString(sc, "Name: ");
            String email = InputValidator.readEmail(sc, "Email: ");
            String phone = InputValidator.readPhone(sc, "Phone: ");
            String password = InputValidator.readNonEmptyString(sc, "Password: ");

            if (repo.findByEmail(email) != null) {
                System.out.println("Email already registered.");
                return;
            }

            int id = nextId();

            Customer customer = new Customer(
                    id,
                    name,
                    email,
                    phone,
                    "C" + id,
                    Customer.hashPassword(password)
            );

            customer.setRegisteredDate(
                    LocalDateTime.now().toString().replace("T", " ")
            );

            if (repo.save(customer)) {
                System.out.println("\nRegistration successful!");
                displayCustomerDetails(customer);
            } else {
                System.out.println("Registration failed.");
            }

        } catch (InputValidator.CustomException e) {
            System.out.println(e.getMessage());
        }
    }

    public Customer customerLogin(Scanner sc) {

        try {
            String email = InputValidator.readEmail(sc, "Email: ");
            String password = InputValidator.readNonEmptyString(sc, "Password: ");

            Customer customer = repo.findByEmailAndPasswordHash(
                    email, Customer.hashPassword(password)
            );

            if (customer != null) {
                System.out.println("\nLogin successful!");
                displayCustomerDetails(customer);
                return customer;
            }

            System.out.println("Invalid email or password.");

        } catch (InputValidator.CustomException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    private void displayCustomerDetails(Customer customer) {

        System.out.println("--------------------------------");
        System.out.println("Welcome, " + customer.getName() + "!");
        System.out.println("Customer ID: " + customer.getId());
        System.out.println("Customer Code: " + customer.getCustomerCode());
        System.out.println("--------------------------------");
    }

    public Admin adminLogin(Scanner sc) {

        try {
            String email = InputValidator.readEmail(sc, "Admin email: ");
            String password = InputValidator.readNonEmptyString(sc, "Password: ");

            Admin admin = repo.findAdminByEmailAndPasswordHash(
                    email, Customer.hashPassword(password)
            );

            if (admin != null) {
                System.out.println("Admin login successful!");
                System.out.println(admin.getFullName());
                return admin;
            }

            System.out.println("Invalid admin credentials.");

        } catch (InputValidator.CustomException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public void updateCustomerDetails(Scanner sc) {

        try {
            if (loggedInCustomer == null && !adminLoggedIn) {
                System.out.println("Please login first.");
                return;
            }

            int id;

            if (adminLoggedIn) {
                id = InputValidator.readInt(sc, "Customer ID: ");
            } else {
                // Customers can update only their own details.
                id = loggedInCustomer.getId();
                System.out.println("Updating your details. Customer ID: " + id);
            }

            Customer customer = repo.findById(id);

            if (customer == null) {
                System.out.println("Customer not found.");
                return;
            }

            System.out.println("Press Enter to keep existing value.");

            System.out.print("New name: ");
            String name = sc.nextLine().trim();

            System.out.print("New phone: ");
            String phone = sc.nextLine().trim();

            if (!name.isEmpty()) {
                customer.setName(name);
            }

            if (!phone.isEmpty()) {

                if (!phone.matches("\\d{10}")) {
                    System.out.println("Invalid phone number.");
                    return;
                }

                customer.setPhone(phone);
            }

            if (repo.update(customer)) {
                System.out.println("Customer updated successfully.");

                if (!adminLoggedIn) {
                    loggedInCustomer = customer;
                }

            } else {
                System.out.println("Update failed.");
            }

        } catch (InputValidator.CustomException e) {
            System.out.println(e.getMessage());
        }
    }

    public Customer findById(int id) {
        return repo.findById(id);
    }

    public List<Customer> getAll() {
        return repo.loadAll();
    }

    public int nextId() {
        return repo.getNextId();
    }

    public void addCustomer(Customer customer) {

        if (repo.findByEmail(customer.getEmail()) != null) {
            System.out.println("Customer already exists.");
            return;
        }

        if (repo.save(customer)) {
            System.out.println("Customer imported: " + customer.getName());
        }
    }
}
