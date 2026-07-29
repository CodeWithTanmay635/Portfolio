package dev.tanmay.contactmanagementsystem.exception;

public class DuplicateContactException extends RuntimeException {
    public DuplicateContactException(String email) {
        super("Duplicate submission detected for email: " + email);
    }
}