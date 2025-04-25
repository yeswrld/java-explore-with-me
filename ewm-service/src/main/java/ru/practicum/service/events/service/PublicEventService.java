package ru.practicum.service.events.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.service.events.dto.EventDto;
import ru.practicum.service.events.dto.EventShortDto;
import ru.practicum.service.events.model.EventUserParams;

import java.util.List;

public interface PublicEventService {
    EventDto getEventById(Long eventId, HttpServletRequest request);

    List<EventShortDto> getAllPublicEvents(EventUserParams userParams, PageRequest pageRequest);
}
