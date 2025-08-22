package org.amerkhaled.eventservice.web.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventRequestDTO(
        @NotBlank(message = "Event name is required")
        @Size(max = 200, message = "Event name must not exceed 200 characters")
        String name,

        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        @NotBlank(message = "Category is required")
        String category,

        @NotNull(message = "Start date is required")
        @Future(message = "Start date must be in the future")
        LocalDateTime startedAt,

        @NotNull(message = "End date is required")
        @Future(message = "End date must be in the future")
        LocalDateTime endedAt,

        @NotNull(message = "Venue ID is required")
        UUID venueId
) {
}
