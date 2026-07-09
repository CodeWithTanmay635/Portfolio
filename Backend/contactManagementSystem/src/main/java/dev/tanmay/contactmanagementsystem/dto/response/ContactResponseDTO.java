package dev.tanmay.contactmanagementsystem.dto.response;

import java.time.Instant;


public record ContactResponseDTO(
    String referenceId,

    Instant submittedAt,

    String website
)
{}
