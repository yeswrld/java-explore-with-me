package ru.practicum.service.events.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.service.events.dto.EventDto;
import ru.practicum.service.events.dto.UpdateEventAdminRequestDto;
import ru.practicum.service.events.model.EventAdminParams;

import java.util.List;

public interface AdminEventService {
    List<EventDto> getAllAdmEvents(EventAdminParams eventAdminParams, PageRequest pageRequest, HttpServletRequest request);

    EventDto updEvent(Long eventId, UpdateEventAdminRequestDto requestDto, HttpServletRequest request);
}
