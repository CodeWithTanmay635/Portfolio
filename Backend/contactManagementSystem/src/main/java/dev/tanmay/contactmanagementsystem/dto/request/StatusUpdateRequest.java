package dev.tanmay.contactmanagementsystem.dto.request;

import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StatusUpdateRequest (
        @NotNull(message = "Status Required")
        MessageStatus newStatus,

        @Size(max = 500, message = "Note max 500 char")
        String note
){}
