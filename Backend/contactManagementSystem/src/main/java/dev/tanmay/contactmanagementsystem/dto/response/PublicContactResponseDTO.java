package dev.tanmay.contactmanagementsystem.dto.response;

import java.time.Instant;
import java.util.UUID;

public record PublicContactResponseDTO(

        UUID id,
        String confirmationMessage,
        Instant receivedAt
) {

    public  static  PublicContactResponseDTO of(UUID id, Instant receivedAt){
        return new PublicContactResponseDTO(
                id,
                "Message receive. Will response shortly",
                receivedAt
        );
    }
}
