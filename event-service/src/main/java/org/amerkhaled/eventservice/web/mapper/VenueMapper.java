package org.amerkhaled.eventservice.web.mapper;

import org.amerkhaled.eventservice.domain.Venue;
import org.amerkhaled.eventservice.web.dto.VenueSummaryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VenueMapper {
    VenueSummaryDTO toSummaryDto(Venue venue);
}
