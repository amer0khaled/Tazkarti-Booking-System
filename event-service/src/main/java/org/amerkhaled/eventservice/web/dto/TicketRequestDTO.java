package org.amerkhaled.eventservice.web.dto;

import jakarta.validation.constraints.*;
import org.amerkhaled.eventservice.domain.TicketType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketRequestDTO(
        @NotNull(message = "Ticket type is required")
        TicketType ticketType,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price must be greater than 0")
        BigDecimal price,

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity,

        @NotNull(message = "Expiration date is required")
        @Future(message = "Expiration date must be in the future")
        LocalDateTime expirationDate,

        @NotNull(message = "Event ID is required")
        UUID eventId
) {
}
