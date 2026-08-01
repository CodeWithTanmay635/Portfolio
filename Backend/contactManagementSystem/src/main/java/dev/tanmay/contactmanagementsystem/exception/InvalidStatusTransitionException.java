package dev.tanmay.contactmanagementsystem.exception;

import dev.tanmay.contactmanagementsystem.model.Contact;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(Contact newStatus) {
        super("Invalid Status Transition" + newStatus.getStatus());
    }
}
