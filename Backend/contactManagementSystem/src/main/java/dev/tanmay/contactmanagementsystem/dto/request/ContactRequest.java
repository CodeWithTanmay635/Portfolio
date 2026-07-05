package dev.tanmay.contactmanagementsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ContactRequest(

        @NotBlank(message = "Name required")
        @Size(min = 2, max = 100, message = "Name 2-100 chars")
        String name,

        @NotBlank(message = "Email required")
        @Email(message = "Enter Valid Message")
        @Size(max = 255)
        String email,

        @NotBlank(message = "Message required")
        @Size(min = 10, max = 2000, message = "Message 10-2000 chars")
        String message,

        String website
){}