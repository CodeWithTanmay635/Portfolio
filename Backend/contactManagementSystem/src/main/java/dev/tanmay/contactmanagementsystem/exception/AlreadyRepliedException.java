package dev.tanmay.contactmanagementsystem.exception;

import dev.tanmay.contactmanagementsystem.model.Contact;

public class AlreadyRepliedException extends RuntimeException {

    public AlreadyRepliedException(String referenceId)
    {
        super("Contact with id " + referenceId + " is already replied");
    }
}
