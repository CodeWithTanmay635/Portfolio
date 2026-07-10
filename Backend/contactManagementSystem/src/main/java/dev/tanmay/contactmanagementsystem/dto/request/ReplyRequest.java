package dev.tanmay.contactmanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReplyRequest(
        @NotBlank(message = "Subject is needed")
        @Size(max = 200, min = 20)
        String message,

        @NotBlank(message = "Body required")
        @Size(max = 5000, min = 100)
        String body

) {
}
