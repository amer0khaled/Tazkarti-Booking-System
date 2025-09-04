package org.amerkhaled.eventservice.service;

import org.amerkhaled.eventservice.domain.Event;
import org.amerkhaled.eventservice.domain.Ticket;

import java.util.List;
import java.util.UUID;

public interface EventService {
    Event createEvent(Event event, UUID venueId);
    Event publishEvent(UUID eventId);
    Event cancelEvent(UUID eventId);
    Event addTicket(UUID eventId, Ticket ticket);
    List<Event> getAllEvents();
    Event getEventById(UUID id);

}
