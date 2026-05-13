package lk.example.myapplication.utils;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SecurityManager {

    private static final int SALT_LENGTH = 16;

    /**
     * Hash a password with a salt using SHA-256
     * @param password The plain text password
     * @return A salted hash of the password in format: salt:hash
     */
    public static String hashPassword(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            String encodedSalt = Base64.encodeToString(salt, Base64.NO_WRAP);
            String encodedHash = Base64.encodeToString(hashedPassword, Base64.NO_WRAP);

            return encodedSalt + ":" + encodedHash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Verify a password against a salted hash
     * @param password The plain text password to verify
     * @param saltedHash The salted hash in format: salt:hash
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String saltedHash) {
        try {
            String[] parts = saltedHash.split(":");
            if (parts.length != 2) {
                return false;
            }

            byte[] salt = Base64.decode(parts[0], Base64.NO_WRAP);
            String storedHash = parts[1];

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            String computedHash = Base64.encodeToString(hashedPassword, Base64.NO_WRAP);

            return computedHash.equals(storedHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * For backward compatibility: simple SHA-256 hash without salt
     * @param password The plain text password
     * @return SHA-256 hash of the password
     */
    public static String hashPasswordSimple(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.encodeToString(hashedPassword, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
