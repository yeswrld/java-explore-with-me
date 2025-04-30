package ru.practicum.service.events.service;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.client.StatsClient;
import ru.practicum.service.ViewService.BaseService;
import ru.practicum.service.events.dto.EventDto;
import ru.practicum.service.events.dto.EventShortDto;
import ru.practicum.service.events.mapper.EventMapper;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.events.model.EventUserParams;
import ru.practicum.service.events.model.State;
import ru.practicum.service.events.storage.EventStorage;
import ru.practicum.service.exception.NotFoundExcep;
import ru.practicum.service.requests.storage.RequestStorage;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.service.events.model.State.PUBLISHED;

@Service

public class PublicEventServiceImpl extends BaseService implements PublicEventService {
    private final EventStorage eventStorage;
    private final RequestStorage requestStorage;
    private final StatsClient statsClient;
    @Value("${app}")
    private String appName;

    public PublicEventServiceImpl(RequestStorage requestStorage, StatsClient statsClient, EventStorage eventStorage, RequestStorage requestStorage1, StatsClient statsClient1) {
        super(requestStorage, statsClient);
        this.eventStorage = eventStorage;
        this.requestStorage = requestStorage1;
        this.statsClient = statsClient1;
    }

    @Override
    public EventDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = eventStorage.findById(eventId)
                .orElseThrow(() -> new NotFoundExcep("Эвент не найден"));

        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundExcep("Событие не опубликовано");
        }
        statsClient.addHit(HitDto.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
        List<Event> eventList = List.of(event);

        return EventMapper.eventToEventDto(event,
                getConfirmedRequests(eventList).getOrDefault(eventId, 0L),
                getViewsForEvents(eventList).getOrDefault(eventId, 0L));
    }

    @Override
    public List<EventShortDto> getAllPublicEvents(EventUserParams params, PageRequest pageRequest, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        Specification<Event> spec = Specification.where((root, query, cb) ->
                cb.equal(root.get("state"), State.PUBLISHED));

        if (params.getText() != null && !params.getText().isBlank()) {
            String searchText = "%" + params.getText().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("annotation")), searchText),
                    cb.like(cb.lower(root.get("description")), searchText)
            ));
        }

        if (params.getCategories() != null && !params.getCategories().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("category").get("id").in(params.getCategories()));
        }

        if (params.getPaid() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("paid"), params.getPaid()));
        }

        LocalDateTime startDate = params.getRangeStart() != null
                ? params.getRangeStart()
                : LocalDateTime.now();
        spec = spec.and((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("eventDate"), startDate));

        if (params.getRangeEnd() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("eventDate"), params.getRangeEnd()));
        }

        Page<Event> eventPage = eventStorage.findAll(spec, pageRequest);
        List<Event> events = eventPage.getContent();

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        if (Boolean.TRUE.equals(params.getOnlyAvailable())) {
            Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
            events = events.stream()
                    .filter(e -> e.getParticipantLimit() == 0 ||
                            e.getParticipantLimit() > confirmedRequests.getOrDefault(e.getId(), 0L))
                    .collect(Collectors.toList());
        }

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Long, Long> viewStats = getViewsForEvents(events);

        return events.stream()
                .map(event -> EventMapper.eventToEventShortDto(
                        event,
                        viewStats.getOrDefault(event.getId(), 0L),
                        confirmedRequests.getOrDefault(event.getId(), 0L)
                ))
                .collect(Collectors.toList());
    }

}

