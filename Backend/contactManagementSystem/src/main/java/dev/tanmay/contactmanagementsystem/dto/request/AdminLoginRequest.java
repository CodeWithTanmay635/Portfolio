package dev.tanmay.contactmanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminLoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {}
