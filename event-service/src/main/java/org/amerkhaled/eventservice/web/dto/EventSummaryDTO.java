package org.amerkhaled.eventservice.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventSummaryDTO(
        UUID id,
        String name,
        String category,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        UUID venueId
) {
}
