package dev.tanmay.contactmanagementsystem.dto.response;

import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContactResponseDTO {
    private UUID id;
    private String message;
    private MessageStatus status;
    private Instant createdAt;
}
