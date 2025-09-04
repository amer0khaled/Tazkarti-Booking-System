package org.amerkhaled.eventservice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.amerkhaled.eventservice.domain.Event;
import org.amerkhaled.eventservice.domain.EventCategory;
import org.amerkhaled.eventservice.domain.EventStatus;
import org.amerkhaled.eventservice.domain.Venue;
import org.amerkhaled.eventservice.service.EventService;
import org.amerkhaled.eventservice.web.dto.EventRequestDTO;
import org.amerkhaled.eventservice.web.dto.EventResponseDTO;
import org.amerkhaled.eventservice.web.dto.EventSummaryDTO;
import org.amerkhaled.eventservice.web.dto.VenueSummaryDTO;
import org.amerkhaled.eventservice.web.mapper.EventMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private EventMapper eventMapper;

    @Test
    void createEvent_returns201AndResponseBody() throws Exception {
        UUID venueId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(2);

        EventRequestDTO req = new EventRequestDTO(
                "Name",
                "Desc",
                "MUSIC",
                start,
                end,
                venueId
        );

        Event entity = Event.builder()
                .name("Name")
                .category(EventCategory.MUSIC)
                .startedAt(start)
                .endedAt(end)
                .build();

        Event saved = Event.builder()
                .id(UUID.randomUUID())
                .name("Name")
                .category(EventCategory.MUSIC)
                .status(EventStatus.DRAFT)
                .startedAt(start)
                .endedAt(end)
                .venue(Venue.builder()
                        .id(venueId)
                        .name("Hall")
                        .capacity(10)
                        .city("C")
                        .state("S")
                        .postalCode("0")
                        .country("X")
                        .build())
                .build();

        EventResponseDTO resp = new EventResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getCategory().name(),
                saved.getStatus().name(),
                start,
                end,
                new VenueSummaryDTO(
                        saved.getVenue().getId(),
                        saved.getVenue().getName(),
                        saved.getVenue().getCapacity(),
                        saved.getVenue().getCity(),
                        saved.getVenue().getCountry()
                )
        );

        when(eventMapper.toEntity(any(EventRequestDTO.class))).thenReturn(entity);
        when(eventService.createEvent(eq(entity), eq(venueId))).thenReturn(saved);
        when(eventMapper.toResponseDto(eq(saved))).thenReturn(resp);

        mockMvc.perform(post("/api/v1/events")
                        .param("venueId", venueId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void publishEvent_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        Event e = Event.builder()
                .id(id)
                .name("N")
                .category(EventCategory.MUSIC)
                .status(EventStatus.PUBLISHED)
                .build();

        EventResponseDTO dto = new EventResponseDTO(
                id, "N", null, "MUSIC", "PUBLISHED", null, null, null
        );

        when(eventService.publishEvent(id)).thenReturn(e);
        when(eventMapper.toResponseDto(e)).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/events/" + id + "/publish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }

    @Test
    void cancelEvent_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        Event e = Event.builder()
                .id(id)
                .name("N")
                .category(EventCategory.MUSIC)
                .status(EventStatus.CANCELLED)
                .build();

        EventResponseDTO dto = new EventResponseDTO(
                id, "N", null, "MUSIC", "CANCELLED", null, null, null
        );

        when(eventService.cancelEvent(id)).thenReturn(e);
        when(eventMapper.toResponseDto(e)).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/events/" + id + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void getAllEvents_returnsList() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Event e1 = Event.builder().id(id1).name("A").category(EventCategory.MUSIC).build();
        Event e2 = Event.builder().id(id2).name("B").category(EventCategory.MUSIC).build();

        when(eventService.getAllEvents()).thenReturn(List.of(e1, e2));
        when(eventMapper.toSummaryDto(e1)).thenReturn(new EventSummaryDTO(id1, "A", "MUSIC", null, null, null));
        when(eventMapper.toSummaryDto(e2)).thenReturn(new EventSummaryDTO(id2, "B", "MUSIC", null, null, null));

        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[1].name").value("B"));
    }

    @Test
    void getEventById_returnsDto() throws Exception {
        UUID id = UUID.randomUUID();
        Event e = Event.builder()
                .id(id)
                .name("A")
                .category(EventCategory.MUSIC)
                .status(EventStatus.DRAFT)
                .build();

        EventResponseDTO dto = new EventResponseDTO(
                id, "A", null, "MUSIC", "DRAFT", null, null, null
        );

        when(eventService.getEventById(id)).thenReturn(e);
        when(eventMapper.toResponseDto(e)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/events/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("A"));
    }
}
