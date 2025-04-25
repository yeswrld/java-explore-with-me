package ru.practicum.service.events.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.service.ViewService.BaseService;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.category.storage.CategoryStorage;
import ru.practicum.service.events.dto.EventDto;
import ru.practicum.service.events.dto.EventShortDto;
import ru.practicum.service.events.dto.EventUpdByUserDto;
import ru.practicum.service.events.dto.NewEventDto;
import ru.practicum.service.events.mapper.EventMapper;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.events.storage.EventStorage;
import ru.practicum.service.exception.AccessExcep;
import ru.practicum.service.exception.NotFoundExcep;
import ru.practicum.service.exception.ValidationExcep;
import ru.practicum.service.location.mapper.LocationMapper;
import ru.practicum.service.location.model.Location;
import ru.practicum.service.location.storage.LocationStorage;
import ru.practicum.service.requests.storage.RequestStorage;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.service.events.model.State.PUBLISHED;

@Slf4j
@Service
public class PrivateEventServiceImpl extends BaseService implements PrivateEventService {
    private final EventStorage eventStorage;
    private final CategoryStorage categoryStorage;
    private final UserStorage userStorage;
    private final LocationStorage locationStorage;
    private final EventMapper eventMapper;

    public PrivateEventServiceImpl(RequestStorage requestStorage, StatsClient statsClient, EventStorage eventStorage, CategoryStorage categoryStorage, UserStorage userStorage, LocationStorage locationStorage, EventMapper eventMapper) {
        super(requestStorage, statsClient);
        this.eventStorage = eventStorage;
        this.categoryStorage = categoryStorage;
        this.userStorage = userStorage;
        this.locationStorage = locationStorage;
        this.eventMapper = eventMapper;
    }

    @Override
    public EventDto addEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate() != null && newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(1)))
            throw new ValidationExcep("Ошибка даты");
        Category category = categoryStorage.findById(newEventDto.getCategory()).orElseThrow(() -> new NotFoundExcep("Категория с запрашиваемым ИД не найдена"));
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundExcep("Пользователь с ИД = " + userId + " не найден"));
        Location location = locationStorage.save(LocationMapper.toLocation(newEventDto.getLocation()));
        Event event = eventStorage.save(EventMapper.newEventDtoToEvent(newEventDto, category, user, location));
        event.setCreatedOn(LocalDateTime.now());
        return EventMapper.eventToEventDto(eventStorage.save(event), null, null);
    }

    @Override
    public EventDto updateEvent(Long userId, Long eventId, EventUpdByUserDto eventUpdByUserDto) {
        if (eventUpdByUserDto.getEventDate() != null && eventUpdByUserDto.getEventDate().isBefore(LocalDateTime.now().plusHours(1)))
            throw new ValidationExcep("Ошибка даты");
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundExcep("Пользователь с ИД = " + userId + " не найден"));
        Event event = eventStorage.findById(eventId).orElseThrow(() -> new NotFoundExcep("Эвент с ИД = " + eventId + " не найден"));
        if (!event.getInitiator().equals(user)) {
            throw new AccessExcep("Пользователь " + user.getName() + " не инициатор события");
        }
        if (event.getState().equals(PUBLISHED)) {
            throw new AccessExcep("Статус события должен быть либо PENDING, либо CANCELED");
        }
        if (eventUpdByUserDto.getCategoryId() != null) {
            Long categoryId = eventUpdByUserDto.getCategoryId();
            Category category = categoryStorage.findById(categoryId).orElseThrow(() -> new NotFoundExcep("Категория с ИД = " + categoryId + " не найдена"));
            event.setCategory(category);
        }
        Event updEvent = EventMapper.updEventFromUserDto(eventUpdByUserDto, event);
        updEvent.setInitiator(user);
        eventStorage.save(updEvent);
        List<Event> eventList = List.of(event);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventList);
        Map<Long, Long> viewStats = getViewsForEvents(eventList);
        return EventMapper.eventToEventDto(event, confirmedRequests.getOrDefault(eventId, 0L),
                viewStats.getOrDefault(eventId, 0L));
    }

    @Override
    public EventDto getEventByUserAdnEventIds(Long userId, Long eventId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundExcep("Пользователь с ИД = " + userId + " не найден"));
        Event event = eventStorage.findById(eventId).orElseThrow(() -> new NotFoundExcep("Эвент с ИД = " + eventId + " не найден"));
        return EventMapper.eventToEventDto(event, null, null);
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, PageRequest pageRequest) {
        return eventStorage.findAllByInitiatorId(userId, pageRequest).stream().map(event -> EventMapper.eventToEventShortDto(event, null, null)).collect(Collectors.toList());
    }
}
