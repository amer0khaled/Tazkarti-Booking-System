package org.amerkhaled.eventservice.web.dto;

import java.util.UUID;

public record VenueSummaryDTO(
        UUID id,
        String name,
        int capacity,
        String city,
        String country
) {
}
