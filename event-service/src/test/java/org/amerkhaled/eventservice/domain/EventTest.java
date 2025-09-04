package org.amerkhaled.eventservice.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void addTicketType_addsWhenWithinCapacity_andSetsBackReference() {
        Venue venue = Venue.builder()
                .id(UUID.randomUUID())
                .name("Main Hall")
                .capacity(100)
                .city("City")
                .state("State")
                .postalCode("00000")
                .country("Country")
                .build();

        Event event = Event.builder()
                .id(UUID.randomUUID())
                .name("Concert")
                .category(EventCategory.MUSIC)
                .status(EventStatus.DRAFT)
                .startedAt(LocalDateTime.now().plusDays(1))
                .endedAt(LocalDateTime.now().plusDays(1).plusHours(2))
                .venue(venue)
                .build();

        Ticket ticket = Ticket.builder()
                .ticketType(TicketType.VIP)
                .price(new BigDecimal("50.00"))
                .quantity(10)
                .remainingQuantity(10)
                .build();

        event.addTicketType(ticket);

        assertEquals(1, event.getTickets().size());
        assertEquals(event, ticket.getEvent());
        assertEquals(10, event.totalAllocatedTickets());
    }

    @Test
    void addTicketType_throwsWhenExceedingCapacity() {
        Venue venue = Venue.builder()
                .id(UUID.randomUUID())
                .name("Main Hall")
                .capacity(5)
                .city("City")
                .state("State")
                .postalCode("00000")
                .country("Country")
                .build();

        Event event = Event.builder()
                .id(UUID.randomUUID())
                .name("Concert")
                .category(EventCategory.MUSIC)
                .status(EventStatus.DRAFT)
                .startedAt(LocalDateTime.now().plusDays(1))
                .endedAt(LocalDateTime.now().plusDays(1).plusHours(2))
                .venue(venue)
                .build();

        Ticket ticket = Ticket.builder()
                .ticketType(TicketType.VIP)
                .price(new BigDecimal("50.00"))
                .quantity(10)
                .remainingQuantity(10)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> event.addTicketType(ticket));
        assertEquals("Cannot exceed venue capacity", ex.getMessage());
        assertTrue(event.getTickets().isEmpty());
    }
}
