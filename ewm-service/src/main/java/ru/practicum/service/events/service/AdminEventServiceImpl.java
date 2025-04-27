package ru.practicum.service.events.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.client.StatsClient;
import ru.practicum.service.ViewService.BaseService;
import ru.practicum.service.category.storage.CategoryStorage;
import ru.practicum.service.events.dto.EventDto;
import ru.practicum.service.events.dto.UpdateEventAdminRequestDto;
import ru.practicum.service.events.mapper.EventMapper;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.events.model.EventAdminParams;
import ru.practicum.service.events.storage.EventStorage;
import ru.practicum.service.exception.NotFoundExcep;
import ru.practicum.service.exception.ValidationExcep;
import ru.practicum.service.exception.ViolationExcep;
import ru.practicum.service.location.mapper.LocationMapper;
import ru.practicum.service.location.storage.LocationStorage;
import ru.practicum.service.requests.storage.RequestStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.service.events.model.State.*;

@Service

public class AdminEventServiceImpl extends BaseService implements AdminEventService {
    private final EventStorage eventStorage;
    private final LocationStorage locationStorage;
    private final CategoryStorage categoryStorage;
    private final StatsClient statsClient;
    @Value("${app}")
    private String appName;

    public AdminEventServiceImpl(RequestStorage requestStorage, StatsClient statsClient, EventStorage eventStorage, LocationStorage locationStorage, CategoryStorage categoryStorage, StatsClient statsClient1) {
        super(requestStorage, statsClient);
        this.eventStorage = eventStorage;
        this.locationStorage = locationStorage;
        this.categoryStorage = categoryStorage;
        this.statsClient = statsClient1;
    }

    @Override
    public List<EventDto> getAllAdmEvents(EventAdminParams eventAdminParams, PageRequest pageRequest, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        Page<Event> eventPage = eventStorage.findAll(pageRequest);

        List<Event> filteredEvents = eventPage.getContent().stream()
                .filter(event -> eventAdminParams.getStates().isEmpty() ||
                        eventAdminParams.getStates().contains(event.getState()))
                .filter(event -> eventAdminParams.getUserIds().isEmpty() ||
                        eventAdminParams.getUserIds().contains(event.getInitiator().getId()))
                .filter(event -> eventAdminParams.getCategoriesIds().isEmpty() ||
                        eventAdminParams.getCategoriesIds().contains(event.getCategory().getId()))
                .filter(event -> eventAdminParams.getStart() == null ||
                        event.getEventDate().isAfter(eventAdminParams.getStart()))
                .filter(event -> eventAdminParams.getEnd() == null ||
                        event.getEventDate().isBefore(eventAdminParams.getEnd()))
                .toList();

        Map<Long, Long> confirmedRequests = getConfirmedRequests(filteredEvents);
        Map<Long, Long> viewStats = getViewsForEvents(filteredEvents);

        return filteredEvents.stream()
                .map(event -> EventMapper.eventToEventDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        viewStats.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    public EventDto updEvent(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        if (updateEventAdminRequestDto.getEventDate() != null
                && updateEventAdminRequestDto.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationExcep("Ошибка даты.");
        }
        Event event = eventStorage.findById(eventId)
                .orElseThrow(() -> new NotFoundExcep("Событие с id: " + eventId + " не найдено."));

        if (!event.getState().equals(PENDING)) {
            throw new ViolationExcep("Дата события не может быть изменена.");
        }
        if (LocalDateTime.now().isAfter(event.getEventDate())) {
            throw new ValidationExcep("Ошибка даты.");
        }

        Optional.ofNullable(updateEventAdminRequestDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventAdminRequestDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventAdminRequestDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);

        if (updateEventAdminRequestDto.getLocation() != null) {
            event.setLocation(locationStorage.save(
                    LocationMapper.toLocation(updateEventAdminRequestDto.getLocation())));
        }

        if (updateEventAdminRequestDto.getCategory() != null) {
            event.setCategory(categoryStorage.findById(updateEventAdminRequestDto.getCategory())
                    .orElseThrow(() -> new NotFoundExcep(
                            "Категория " + updateEventAdminRequestDto.getCategory() + " не найдена")));
        }

        Optional.ofNullable(updateEventAdminRequestDto.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventAdminRequestDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventAdminRequestDto.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updateEventAdminRequestDto.getTitle()).ifPresent(event::setTitle);

        if (updateEventAdminRequestDto.getStateAction() != null) {
            switch (updateEventAdminRequestDto.getStateAction()) {
                case "REJECT_EVENT":
                    event.setState(CANCELED);
                    break;
                case "PUBLISH_EVENT":
                    event.setPublishedOn(LocalDateTime.now());
                    event.setState(PUBLISHED);
                    break;
                default:
                    throw new ValidationExcep(
                            "Такого действия не существует - " + updateEventAdminRequestDto.getStateAction());
            }
        }

        Event updatedEvent = eventStorage.save(event);
        return EventMapper.eventToEventDto(updatedEvent, 0L, 0L);
    }
}
