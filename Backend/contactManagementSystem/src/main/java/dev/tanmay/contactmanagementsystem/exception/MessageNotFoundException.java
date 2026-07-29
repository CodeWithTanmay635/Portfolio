package dev.tanmay.contactmanagementsystem.exception;


import java.util.UUID;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(UUID id) {
        super("Message not found with id: " + id);
    }
}

//Simple custom exception. RuntimeException so it's unchecked — you don't have to declare throws everywhere.
// GlobalExceptionHandler (coming soon) will catch this and turn it into a clean 404 response
// instead of a raw stack trace.
