
package model;

public class Admin extends Person {

    private final String adminCode;
    private final String passwordHash;

    public Admin(int id, String name, String email,
                 String phone, String adminCode,
                 String passwordHash) {

        super(id, name, email, phone);

        this.adminCode = adminCode;
        this.passwordHash = passwordHash;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public String getFullName() {

        return "Admin: " + getName() +
                " [" + adminCode + "]";
    }
}
