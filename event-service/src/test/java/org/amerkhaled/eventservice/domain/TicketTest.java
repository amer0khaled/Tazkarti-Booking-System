package org.amerkhaled.eventservice.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    @Test
    void reserve_reducesRemaining_andUpdatesStatus() {
        Ticket ticket = Ticket.builder()
                .ticketType(TicketType.STANDARD)
                .price(new BigDecimal("10.00"))
                .quantity(10)
                .remainingQuantity(10)
                .build();

        ticket.reserve(3);

        assertEquals(7, ticket.getRemainingQuantity());
        assertEquals(TicketStatus.RESERVED, ticket.getTicketStatus());

        ticket.reserve(7);
        assertEquals(0, ticket.getRemainingQuantity());
        assertEquals(TicketStatus.BOOKED, ticket.getTicketStatus());
    }

    @Test
    void reserve_throwsWhenNotEnoughRemaining() {
        Ticket ticket = Ticket.builder()
                .ticketType(TicketType.STANDARD)
                .price(new BigDecimal("10.00"))
                .quantity(5)
                .remainingQuantity(2)
                .build();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> ticket.reserve(3));
        assertEquals("Not enough tickets available", ex.getMessage());
        assertEquals(2, ticket.getRemainingQuantity());
        assertEquals(TicketStatus.AVAILABLE, ticket.getTicketStatus());
    }

    @Test
    void release_increasesRemaining_andSetsAvailable() {
        Ticket ticket = Ticket.builder()
                .ticketType(TicketType.STANDARD)
                .price(new BigDecimal("10.00"))
                .quantity(10)
                .remainingQuantity(5)
                .ticketStatus(TicketStatus.RESERVED)
                .build();

        ticket.release(2);

        assertEquals(7, ticket.getRemainingQuantity());
        assertEquals(TicketStatus.AVAILABLE, ticket.getTicketStatus());
    }
}
