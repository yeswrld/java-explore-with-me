package ru.practicum.service.events.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.service.events.dto.EventDto;
import ru.practicum.service.events.dto.EventShortDto;
import ru.practicum.service.events.dto.EventUpdByUserDto;
import ru.practicum.service.events.dto.NewEventDto;

import java.util.List;

public interface PrivateEventService {
    EventDto addEvent(Long userId, NewEventDto newEventDto);

    EventDto updateEvent(Long userId, Long eventId, EventUpdByUserDto eventUpdByUserDto);

    EventDto getEventByUserAdnEventIds(Long userId, Long eventId);

    List<EventShortDto> getEventsByUserId(Long userId, PageRequest pageRequest);
}
