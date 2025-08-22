package org.amerkhaled.eventservice.web.mapper;

import org.amerkhaled.eventservice.domain.Ticket;
import org.amerkhaled.eventservice.web.dto.TicketRequestDTO;
import org.amerkhaled.eventservice.web.dto.TicketResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface TicketMapper {
    Ticket toEntity(TicketRequestDTO dto);

    TicketResponseDTO toResponseDto(Ticket ticket);
}
