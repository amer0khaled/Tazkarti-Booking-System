package org.amerkhaled.eventservice.web.dto;

import org.amerkhaled.eventservice.domain.TicketStatus;
import org.amerkhaled.eventservice.domain.TicketType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketResponseDTO(
        UUID id,
        TicketType ticketType,
        TicketStatus ticketStatus,
        BigDecimal price,
        int quantity,
        int remainingQuantity,
        LocalDateTime expirationDate,
        EventSummaryDTO event
) {
}
