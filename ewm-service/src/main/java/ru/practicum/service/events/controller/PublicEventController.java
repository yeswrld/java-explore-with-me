package ru.practicum.service.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.events.dto.EventDto;
import ru.practicum.service.events.dto.EventShortDto;
import ru.practicum.service.events.model.EventSort;
import ru.practicum.service.events.model.EventUserParams;
import ru.practicum.service.events.service.PublicEventService;
import ru.practicum.service.exception.ValidationExcep;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {
    private final PublicEventService eventService;
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @GetMapping
    public List<EventShortDto> getAllPublicEvents(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = "") List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime rangeStart,
            @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) EventSort sort,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            HttpServletRequest request) {
        log.info("PUBLIC ==>> Получение событий /events");
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationExcep("Ошибка даты");
        }
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return eventService.getAllPublicEvents(
                new EventUserParams(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, request), pageRequest, request);
    }

    @GetMapping("/{eventId}")
    public EventDto getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("PUBLIC ==>> Получение эвента с ИД = {}", eventId);
        return eventService.getEventById(eventId, request);
    }
}
