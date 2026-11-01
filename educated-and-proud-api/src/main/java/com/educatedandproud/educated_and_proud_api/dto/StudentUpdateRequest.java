package com.educatedandproud.educated_and_proud_api.dto;

import jakarta.validation.constraints.*;

public record StudentUpdateRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Please enter a valid email address")
        String email,

        @NotBlank(message = "Current school is required")
        String schoolAttending,

        String schoolTransferringTo,

        @NotNull(message = "Estimated graduation year is required")
        @Min(value = 2020, message = "Please enter a valid graduation year")
        @Max(value = 2100, message = "Please enter a valid graduation year")
        Integer estimatedGradYear,

        String major
) {}