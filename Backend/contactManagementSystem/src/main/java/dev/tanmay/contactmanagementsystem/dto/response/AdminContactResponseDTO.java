package dev.tanmay.contactmanagementsystem.dto.response;

import dev.tanmay.contactmanagementsystem.model.Contact;
import dev.tanmay.contactmanagementsystem.model.enums.MessagePriority;
import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;

import java.time.Instant;
import java.util.UUID;

public record AdminContactResponseDTO(
        UUID id,
        String name,
        String email,
        String message,
        MessageStatus status,
        MessagePriority priority,
        int priorityScore,
        String adminNotes,
        Instant createdAt,
        Instant updatedAt,
        Instant repliedAt
) {
    public static AdminContactResponseDTO from(Contact contact) {
        return new AdminContactResponseDTO(
                contact.getId(),
                contact.getName(),
                contact.getEmail(),
                contact.getMessage(),
                contact.getStatus(),
                contact.getPriority(),
                contact.getPriorityScore(),
                contact.getAdminNote(),
                contact.getCreatedAt(),
                contact.getUpdatedAt(),
                contact.getRepliedAt()
        );
    }
}
