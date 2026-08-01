package dev.tanmay.contactmanagementsystem.exception;

import java.util.UUID;

public class ContactNotFoundException extends RuntimeException {
    public ContactNotFoundException(UUID id) {
        super("Contact Not Found" + id.toString());
    }
}
