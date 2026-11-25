
package model;

public class Customer extends Person {

    private String customerCode;
    private String passwordHash;
    private String registeredDate;

    public Customer(int id, String name, String email,
                    String phone, String customerCode,
                    String passwordHash) {

        super(id, name, email, phone);

        this.customerCode = customerCode;
        this.passwordHash = passwordHash;
    }

    public Customer(int id, String name, String email, String phone) {

        this(id, name, email, phone, "C" + id, "");
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }

    // Retained for compatibility with the existing database.
    // This is educational hashing, not secure password storage.
    public static String hashPassword(String raw) {

        StringBuilder sb = new StringBuilder();

        for (char c : raw.toCharArray()) {
            sb.append(Integer.toHexString(c + 17));
        }

        return sb.toString();
    }

    public boolean checkPassword(String raw) {

        return this.passwordHash.equals(hashPassword(raw));
    }

    @Override
    public String getFullName() {

        return "Customer: " + getName() +
                " (" + customerCode + ")";
    }
}
