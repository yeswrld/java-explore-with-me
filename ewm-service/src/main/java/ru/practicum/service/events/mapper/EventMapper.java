package ru.practicum.service.events.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.client.StatsClient;
import ru.practicum.service.category.mapper.CategoryMapper;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.events.dto.EventDto;
import ru.practicum.service.events.dto.EventShortDto;
import ru.practicum.service.events.dto.EventUpdByUserDto;
import ru.practicum.service.events.dto.NewEventDto;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.events.model.State;
import ru.practicum.service.exception.NotFoundExcep;
import ru.practicum.service.location.mapper.LocationMapper;
import ru.practicum.service.location.model.Location;
import ru.practicum.service.user.mapper.UserMapper;
import ru.practicum.service.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class EventMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsClient statsClient;

    public static Event newEventDtoToEvent(NewEventDto dto, Category category, User user, Location location) {

        return new Event(
                0L,
                user,
                dto.getAnnotation(),
                category,
                LocalDateTime.now(),
                dto.getDescription(),
                dto.getEventDate(),
                location,
                dto.getPaid(),
                dto.getParticipantLimit(),
                null,
                dto.getRequestModeration(),
                State.PENDING,
                dto.getTitle());
    }

    public static EventDto eventToEventDto(Event event, Long confirmedRequests, Long views) {
        String publishedOn = null;
        if (event.getPublishedOn() != null) {
            publishedOn = event.getPublishedOn().format(formatter);
        }
        return new EventDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getPaid(),
                event.getEventDate().format(formatter),
                UserMapper.toShortUserDto(event.getInitiator()),
                event.getDescription(),
                event.getParticipantLimit(),
                event.getState(),
                event.getCreatedOn().format(formatter),
                LocationMapper.toLocationDto(event.getLocation()),
                event.getRequestModeration(),
                confirmedRequests,
                publishedOn,
                views
        );
    }

    public static Event updEventFromUserDto(EventUpdByUserDto dto, Event event) {
        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {
            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            event.setDescription(dto.getDescription());
        }

        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            event.setTitle(dto.getTitle());
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction().toUpperCase()) {
                case "SEND_TO_REVIEW":
                    event.setState(State.PENDING);
                    break;
                case "CANCEL_REVIEW":
                    event.setState(State.CANCELED);
                    break;
                default:
                    throw new NotFoundExcep("Состояние " + dto.getStateAction() + " не известно");
            }
        }
        return event;
    }

    public static EventShortDto eventToEventShortDto(Event event, Long views, Long confirmed) {
        return new EventShortDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                confirmed,
                event.getEventDate().format(formatter),
                event.getId(),
                UserMapper.toShortUserDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                views);
    }

    public static List<EventShortDto> eventListToEventShortDtoList(List<Event> events,
                                                                   Map<Long, Long> viewStatMap,
                                                                   Map<Long, Long> confirmedRequests) {
        List<EventShortDto> dtos = new ArrayList<>();
        for (Event event : events) {
            Long views = viewStatMap.getOrDefault(event.getId(), 0L);
            Long confirmedRequestsCount = confirmedRequests.getOrDefault(event.getId(), 0L);
            dtos.add(new EventShortDto(
                    event.getAnnotation(),
                    CategoryMapper.toCategoryDto(event.getCategory()),
                    confirmedRequestsCount,
                    event.getEventDate().format(formatter),
                    event.getId(),
                    UserMapper.toShortUserDto(event.getInitiator()),
                    event.getPaid(),
                    event.getTitle(),
                    views
            ));
        }
        dtos.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        return dtos;
    }
}