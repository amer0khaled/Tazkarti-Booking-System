package org.amerkhaled.eventservice.web.mapper;

import org.amerkhaled.eventservice.domain.Event;
import org.amerkhaled.eventservice.web.dto.EventRequestDTO;
import org.amerkhaled.eventservice.web.dto.EventResponseDTO;
import org.amerkhaled.eventservice.web.dto.EventSummaryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {VenueMapper.class})
public interface EventMapper {
    Event toEntity(EventRequestDTO dto);

    EventResponseDTO toResponseDto(Event event);

    EventSummaryDTO toSummaryDto(Event event);
}
