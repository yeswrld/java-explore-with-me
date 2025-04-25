package ru.practicum.service.events.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.service.events.dto.EventDto;
import ru.practicum.service.events.dto.UpdateEventAdminRequestDto;
import ru.practicum.service.events.model.EventAdminParams;

import java.util.List;

public interface AdminEventService {
    List<EventDto> getAllAdmEvents(EventAdminParams eventAdminParams, PageRequest pageRequest);

    EventDto updEvent(Long eventId, UpdateEventAdminRequestDto requestDto);
}
