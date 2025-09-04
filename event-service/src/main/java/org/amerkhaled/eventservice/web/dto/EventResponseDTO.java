package org.amerkhaled.eventservice.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventResponseDTO(
        UUID id,
        String name,
        String description,
        String category,
        String status,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        VenueSummaryDTO venue
) {
}
