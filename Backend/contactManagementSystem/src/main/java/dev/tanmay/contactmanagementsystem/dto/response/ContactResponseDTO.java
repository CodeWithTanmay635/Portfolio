package dev.tanmay.contactmanagementsystem.dto.response;

import dev.tanmay.contactmanagementsystem.model.Contact;

import java.time.Instant;
import java.util.UUID;


public record ContactResponseDTO(
    UUID referenceId,

    String confirmationMessage,

    Instant submittedAt
)
{

    public static ContactResponseDTO from(Contact contact){
        return new ContactResponseDTO(
               contact.getId(),
                "Message Received. Will Respond Shortly!!",
                contact.getCreatedAt()
        );
    }
}
