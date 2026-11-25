
package util;

import java.math.BigDecimal;
import java.util.Scanner;

public class InputValidator {

    public static class CustomException extends Exception {
        private static final long serialVersionUID = 1L;

        public CustomException(String message) {
            super(message);
        }
    }

    public static String readNonEmptyString(Scanner sc, String prompt)
            throws CustomException {

        System.out.print(prompt);
        String value = sc.nextLine().trim();

        if (value.isEmpty()) {
            throw new CustomException("Field cannot be empty.");
        }

        return value;
    }

    public static String readEmail(Scanner sc, String prompt)
            throws CustomException {

        String email = readNonEmptyString(sc, prompt);

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new CustomException("Invalid email format.");
        }

        return email;
    }

    public static String readPhone(Scanner sc, String prompt)
            throws CustomException {

        String phone = readNonEmptyString(sc, prompt);

        if (!phone.matches("\\d{10}")) {
            throw new CustomException("Phone must contain 10 digits.");
        }

        return phone;
    }

    public static int readInt(Scanner sc, String prompt)
            throws CustomException {

        System.out.print(prompt);

        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new CustomException("Invalid integer input.");
        }
    }

    public static double readDouble(Scanner sc, String prompt)
            throws CustomException {

        System.out.print(prompt);

        try {
            double value = Double.parseDouble(sc.nextLine().trim());

            if (!Double.isFinite(value)) {
                throw new CustomException("Invalid numeric value.");
            }

            return value;

        } catch (NumberFormatException e) {
            throw new CustomException("Invalid numeric input.");
        }
    }

    public static BigDecimal readBigDecimal(Scanner sc, String prompt)
            throws CustomException {

        System.out.print(prompt);

        try {
            return new BigDecimal(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new CustomException("Invalid decimal input.");
        }
    }

    public static void validatePositive(double amount)
            throws CustomException {

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new CustomException("Amount must be positive.");
        }
    }

    public static void validatePositive(BigDecimal amount)
            throws CustomException {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new CustomException("Amount must be positive.");
        }
    }
}

