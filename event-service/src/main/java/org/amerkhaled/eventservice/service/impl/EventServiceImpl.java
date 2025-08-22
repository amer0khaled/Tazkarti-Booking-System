package org.amerkhaled.eventservice.service.impl;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityNotFoundException;
import org.amerkhaled.eventservice.domain.Event;
import org.amerkhaled.eventservice.domain.EventStatus;
import org.amerkhaled.eventservice.domain.Ticket;
import org.amerkhaled.eventservice.domain.Venue;
import org.amerkhaled.eventservice.repository.EventRepository;
import org.amerkhaled.eventservice.repository.TicketRepository;
import org.amerkhaled.eventservice.repository.VenueRepository;
import org.amerkhaled.eventservice.service.EventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final TicketRepository ticketRepository;

    public EventServiceImpl(EventRepository eventRepository,
                            VenueRepository venueRepository,
                            TicketRepository ticketRepository) {

        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.ticketRepository = ticketRepository;

    }


    @Override
    public Event createEvent(Event event, UUID venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new EntityNotFoundException("Venue not found"));

        event.setVenue(venue);

        return eventRepository.save(event);
    }

    @Override
    public Event publishEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        event.setStatus(EventStatus.PUBLISHED);
        return eventRepository.save(event);
    }

    @Override
    public Event cancelEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not Found"));

        event.setStatus(EventStatus.CANCELLED);
        return eventRepository.save(event);
    }

    @Override
    public Event addTicket(UUID eventId, Ticket ticket) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        event.addTicketType(ticket);
        return eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Event getEventById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not Found"));
    }
}
