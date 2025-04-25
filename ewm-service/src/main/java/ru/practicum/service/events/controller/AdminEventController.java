package ru.practicum.service.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.client.StatsClient;
import ru.practicum.service.events.dto.EventDto;
import ru.practicum.service.events.dto.UpdateEventAdminRequestDto;
import ru.practicum.service.events.model.EventAdminParams;
import ru.practicum.service.events.model.State;
import ru.practicum.service.events.service.AdminEventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    private final AdminEventService adminEventService;
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final StatsClient statsClient;

    @GetMapping
    public List<EventDto> getAllAdminEvents(@RequestParam(defaultValue = "") List<Long> users,
                                            @RequestParam(defaultValue = "") List<String> states,
                                            @RequestParam(defaultValue = "") List<Long> categories,
                                            @RequestParam(required = false)
                                            @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime rangeStart,
                                            @RequestParam(required = false)
                                            @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime rangeEnd,
                                            @RequestParam(value = "from", defaultValue = "0") Integer from,
                                            @RequestParam(value = "size", defaultValue = "10") Integer size,
                                            HttpServletRequest request) {        statsClient.addHit(new HitDto(null,
            "ewm-service",
            request.getRequestURI(),
            request.getRemoteAddr(),
            LocalDateTime.now()
    ));

        log.info("ADMIN ==>>: Поиск эвентов");
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());
        List<State> statesList = states.stream()
                .map(String::toUpperCase)
                .map(State::valueOf)
                .toList();

        return adminEventService.getAllAdmEvents(
                new EventAdminParams(users, statesList, categories, rangeStart, rangeEnd),
                pageRequest);
    }

    @PatchMapping("/{eventId}")
    public EventDto admUpdate(@PathVariable Long eventId, @RequestBody @Valid UpdateEventAdminRequestDto eventAdminRequestDto,
                              HttpServletRequest request) {
        statsClient.addHit(new HitDto(null,
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));
        log.info("ADMIN ==>>: Обновление эвента {} и его статуса", eventAdminRequestDto);
        return adminEventService.updEvent(eventId, eventAdminRequestDto);
    }
}