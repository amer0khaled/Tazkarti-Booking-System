package org.amerkhaled.eventservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.amerkhaled.eventservice.domain.*;
import org.amerkhaled.eventservice.repository.EventRepository;
import org.amerkhaled.eventservice.repository.TicketRepository;
import org.amerkhaled.eventservice.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceImplTest {

    private EventRepository eventRepository;
    private VenueRepository venueRepository;
    private TicketRepository ticketRepository;
    private EventServiceImpl service;

    @BeforeEach
    void setup() {
        eventRepository = mock(EventRepository.class);
        venueRepository = mock(VenueRepository.class);
        ticketRepository = mock(TicketRepository.class);
        service = new EventServiceImpl(eventRepository, venueRepository, ticketRepository);
    }

    @Test
    void createEvent_setsVenueAndSaves() {
        UUID venueId = UUID.randomUUID();
        Venue venue = Venue.builder().id(venueId).name("Hall").capacity(100).city("City").state("State").postalCode("00000").country("Country").build();
        Event event = Event.builder()
                .name("Show")
                .category(EventCategory.MUSIC)
                .startedAt(LocalDateTime.now().plusDays(1))
                .endedAt(LocalDateTime.now().plusDays(2))
                .build();

        when(venueRepository.findById(venueId)).thenReturn(Optional.of(venue));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        Event created = service.createEvent(event, venueId);

        assertEquals(venue, created.getVenue());
        verify(eventRepository).save(created);
    }

    @Test
    void createEvent_throwsWhenVenueNotFound() {
        UUID venueId = UUID.randomUUID();
        when(venueRepository.findById(venueId)).thenReturn(Optional.empty());

        Event event = Event.builder().name("Show").category(EventCategory.MUSIC)
                .startedAt(LocalDateTime.now().plusDays(1)).endedAt(LocalDateTime.now().plusDays(2)).build();

        assertThrows(EntityNotFoundException.class, () -> service.createEvent(event, venueId));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void publishEvent_setsStatusAndSaves() {
        UUID id = UUID.randomUUID();
        Event event = Event.builder().id(id).name("E").category(EventCategory.MUSIC)
                .startedAt(LocalDateTime.now().plusDays(1)).endedAt(LocalDateTime.now().plusDays(2)).build();
        when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        Event updated = service.publishEvent(id);

        assertEquals(EventStatus.PUBLISHED, updated.getStatus());
        verify(eventRepository).save(event);
    }

    @Test
    void publishEvent_throwsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(eventRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.publishEvent(id));
    }

    @Test
    void cancelEvent_setsStatusAndSaves() {
        UUID id = UUID.randomUUID();
        Event event = Event.builder().id(id).name("E").category(EventCategory.MUSIC)
                .startedAt(LocalDateTime.now().plusDays(1)).endedAt(LocalDateTime.now().plusDays(2)).build();
        when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        Event updated = service.cancelEvent(id);

        assertEquals(EventStatus.CANCELLED, updated.getStatus());
        verify(eventRepository).save(event);
    }

    @Test
    void cancelEvent_throwsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(eventRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.cancelEvent(id));
    }

    @Test
    void addTicket_addsTicketAndSaves() {
        UUID id = UUID.randomUUID();
        Venue venue = Venue.builder().id(UUID.randomUUID()).name("Hall").capacity(100).city("City").state("State").postalCode("00000").country("Country").build();
        Event event = Event.builder().id(id).name("E").category(EventCategory.MUSIC)
                .startedAt(LocalDateTime.now().plusDays(1)).endedAt(LocalDateTime.now().plusDays(2)).venue(venue).build();
        when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket ticket = Ticket.builder().ticketType(TicketType.STANDARD).price(new BigDecimal("20.00")).quantity(5).remainingQuantity(5).build();

        Event updated = service.addTicket(id, ticket);

        assertEquals(1, updated.getTickets().size());
        verify(eventRepository).save(event);
    }

    @Test
    void addTicket_throwsWhenEventNotFound() {
        UUID id = UUID.randomUUID();
        when(eventRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.addTicket(id, Ticket.builder().build()));
    }

    @Test
    void getAllEvents_delegatesToRepository() {
        when(eventRepository.findAll()).thenReturn(List.of(Event.builder().build()));
        List<Event> result = service.getAllEvents();
        assertEquals(1, result.size());
        verify(eventRepository).findAll();
    }

    @Test
    void getEventById_returnsEventOrThrows() {
        UUID id = UUID.randomUUID();
        Event e = Event.builder().id(id).name("E").category(EventCategory.MUSIC)
                .startedAt(LocalDateTime.now().plusDays(1)).endedAt(LocalDateTime.now().plusDays(2)).build();
        when(eventRepository.findById(id)).thenReturn(Optional.of(e));
        assertEquals(e, service.getEventById(id));

        when(eventRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getEventById(id));
    }
}
