package dev.tanmay.contactmanagementsystem.dto.response;

import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;


public record ContactResponseDTO (
    String referenceId,

    String message,

    Instant submittedAt
    )
{}
