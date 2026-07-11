package dev.tanmay.contactmanagementsystem.dto.response;

import java.time.Instant;
import java.util.UUID;

public record PublicContactResponseDTO(

        UUID id,
        String confirmationMessage,
        Instant receivedAt
) {
}
