package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validations {

    public static boolean isEmailValid(String email) {

        return email.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

    }

    public static boolean isPasswordValid(String password) {

        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$");

    }

    public static boolean isDouble(String text) {

        return text.matches("^\\d+(\\.\\d{2})?$");

    }

    public static boolean isInteger(String text) {

        return text.matches("^\\d+$");

    }

    public static boolean isMobileNumber(String mobile) {

        return mobile.matches("^07[01245678]{1}[0-9]{7}$");

    }

    public static boolean isValidDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {

            LocalDate.parse(dateString, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
