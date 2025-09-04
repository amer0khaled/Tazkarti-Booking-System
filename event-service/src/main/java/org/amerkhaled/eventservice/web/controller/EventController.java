package org.amerkhaled.eventservice.web.controller;

import jakarta.validation.Valid;
import org.amerkhaled.eventservice.domain.Event;
import org.amerkhaled.eventservice.domain.Ticket;
import org.amerkhaled.eventservice.service.EventService;
import org.amerkhaled.eventservice.web.dto.*;
import org.amerkhaled.eventservice.web.mapper.EventMapper;
import org.amerkhaled.eventservice.web.mapper.TicketMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;
    private final TicketMapper ticketMapper;

    public EventController(EventService eventService,
                           EventMapper eventMapper,
                           TicketMapper ticketMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
        this.ticketMapper = ticketMapper;
    }

    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(
            @Valid @RequestBody EventRequestDTO dto) {

        Event event = eventMapper.toEntity(dto);
        Event created = eventService.createEvent(event, dto.venueId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventMapper.toResponseDto(created));
    }


    @PatchMapping("/{id}/publish")
    public ResponseEntity<EventResponseDTO> publishEvent(@PathVariable UUID id) {
        Event published = eventService.publishEvent(id);
        return ResponseEntity.ok(eventMapper.toResponseDto(published));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<EventResponseDTO> cancelEvent(@PathVariable UUID id) {
        Event cancelled = eventService.cancelEvent(id);
        return ResponseEntity.ok(eventMapper.toResponseDto(cancelled));
    }

    @PostMapping("/{id}/tickets")
    public ResponseEntity<EventResponseDTO> addTicket(
            @PathVariable UUID id,
            @Valid @RequestBody TicketRequestDTO dto) {

        Ticket ticket = ticketMapper.toEntity(dto);
        Event updated = eventService.addTicket(id, ticket);

        return ResponseEntity.ok(eventMapper.toResponseDto(updated));
    }

    @GetMapping
    public ResponseEntity<List<EventSummaryDTO>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(
                events.stream()
                        .map(eventMapper::toSummaryDto)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable UUID id) {
        Event event = eventService.getEventById(id);
        return ResponseEntity.ok(eventMapper.toResponseDto(event));
    }
}
