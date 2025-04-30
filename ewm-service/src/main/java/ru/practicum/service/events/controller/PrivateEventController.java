package ru.practicum.service.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.events.dto.EventDto;
import ru.practicum.service.events.dto.EventShortDto;
import ru.practicum.service.events.dto.EventUpdByUserDto;
import ru.practicum.service.events.dto.NewEventDto;
import ru.practicum.service.events.service.PrivateEventService;
import ru.practicum.service.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.service.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.service.requests.dto.ParticipationRequestDto;
import ru.practicum.service.requests.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final PrivateEventService privateEventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto add(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto, HttpServletRequest request) {
        log.info(" USER ==>> Добавление эвента {}, пользователем с ИД = {}", newEventDto, userId);
        return privateEventService.addEvent(userId, newEventDto, request);
    }

    @PatchMapping("/{eventId}")
    public EventDto update(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody @Valid EventUpdByUserDto eventUpdByUserDto, HttpServletRequest request) {
        log.info(" USER ==>> Изменение эвента c ИД = {}, пользователем с ИД = {}", eventId, userId);
        return privateEventService.updateEvent(userId, eventId, eventUpdByUserDto, request);
    }

    @GetMapping("/{eventId}")
    public EventDto getByUserId(@PathVariable Long userId, @PathVariable Long eventId, HttpServletRequest request) {
        log.info(" USER ==>> Получение эвента c ИД = {} пользователя с ИД = {}", eventId, userId);
        return privateEventService.getEventByUserAdnEventIds(userId, eventId, request);
    }

    @GetMapping
    public List<EventShortDto> getEventsByUser(
            @PathVariable @Positive Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10") @Positive int to, HttpServletRequest request) {
        log.info(" USER ==>> Получение эвентов, добавленных пользователем с идентификатором {}", userId);
        int page = from / to;
        PageRequest pageRequest = PageRequest.of(page, to);
        return privateEventService.getEventsByUserId(userId, pageRequest, request);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId, @PathVariable Long eventId, HttpServletRequest request) {
        log.info(" USER ==>> Получение запросов на участие в эвенте с ИД = {}, пользователем с ИД = {}", eventId, userId);
        return requestService.getEventRequests(userId, eventId, request);
    }

    @PatchMapping({"/{eventId}/requests", "/{eventId}/requests/"})
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody @Valid EventRequestStatusUpdateRequest updateRequest, HttpServletRequest request) {
        log.info(" USER ==>> Изминение статуса заявок на участие в эвенте {}, пользователя с ИД = {}", eventId, userId);
        return requestService.updateRequestStatus(userId, eventId, updateRequest, request);
    }
}