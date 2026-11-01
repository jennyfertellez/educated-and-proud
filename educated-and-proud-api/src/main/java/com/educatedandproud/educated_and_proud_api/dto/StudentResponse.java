package com.educatedandproud.educated_and_proud_api.dto;

import java.time.LocalDateTime;

public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String schoolAttending,
        String schoolTransferringTo,
        Integer estimatedGradYear,
        String major,
        LocalDateTime createdAt
) {}