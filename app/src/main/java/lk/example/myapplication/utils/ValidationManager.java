package lk.example.myapplication.utils;

import android.util.Patterns;

public class ValidationManager {

    /**
     * Validate if a string is empty or null
     * @param text The text to validate
     * @return true if text is valid (not empty), false otherwise
     */
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Validate email format
     * @param email The email to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validate username format (alphanumeric, underscores, dashes allowed, 3-20 characters)
     * @param username The username to validate
     * @return true if username is valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (!isNotEmpty(username)) {
            return false;
        }
        if (username.length() < 3 || username.length() > 20) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_-]+$");
    }

    /**
     * Validate password strength
     * @param password The password to validate
     * @return true if password meets minimum requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (!isNotEmpty(password)) {
            return false;
        }
        // Minimum 6 characters
        return password.length() >= 6;
    }

    /**
     * Validate task title
     * @param title The task title to validate
     * @return true if title is valid, false otherwise
     */
    public static boolean isValidTaskTitle(String title) {
        if (!isNotEmpty(title)) {
            return false;
        }
        return title.length() <= 200;
    }

    /**
     * Get validation error message for email
     * @param email The email to check
     * @return Error message if invalid, null if valid
     */
    public static String getEmailError(String email) {
        if (!isNotEmpty(email)) {
            return "Email cannot be empty";
        }
        if (!isValidEmail(email)) {
            return "Please enter a valid email address";
        }
        return null;
    }

    /**
     * Get validation error message for username
     * @param username The username to check
     * @return Error message if invalid, null if valid
     */
    public static String getUsernameError(String username) {
        if (!isNotEmpty(username)) {
            return "Username cannot be empty";
        }
        if (username.length() < 3) {
            return "Username must be at least 3 characters";
        }
        if (username.length() > 20) {
            return "Username must be at most 20 characters";
        }
        if (!username.matches("^[a-zA-Z0-9_-]+$")) {
            return "Username can only contain letters, numbers, underscores, and dashes";
        }
        return null;
    }

    /**
     * Get validation error message for password
     * @param password The password to check
     * @return Error message if invalid, null if valid
     */
    public static String getPasswordError(String password) {
        if (!isNotEmpty(password)) {
            return "Password cannot be empty";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters";
        }
        return null;
    }

    /**
     * Validate that two passwords match
     * @param password The first password
     * @param confirmPassword The confirm password
     * @return true if both passwords match, false otherwise
     */
    public static boolean passwordsMatch(String password, String confirmPassword) {
        return isNotEmpty(password) && isNotEmpty(confirmPassword) && password.equals(confirmPassword);
    }
}
