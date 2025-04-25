package ru.practicum.service.ViewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ViewStatsDto;
import ru.practicum.client.StatsClient;
import ru.practicum.service.compilations.model.Compilation;
import ru.practicum.service.events.dto.CountDto;
import ru.practicum.service.events.dto.EventShortDto;
import ru.practicum.service.events.mapper.EventMapper;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.requests.storage.RequestStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.service.requests.model.Status.CONFIRMED;

@Slf4j
@RequiredArgsConstructor
@Service
public abstract class BaseService {
    private final RequestStorage requestStorage;
    private final StatsClient statsClient;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /*protected Map<Long, Long> getViewsForEvents(List<Event> events) {
        Map<String, Long> eventUrisAndIds = events.stream()
                .collect(Collectors.toMap(
                        event -> String.format("/events/%s", event.getId()),
                        Event::getId
                ));
        LocalDateTime startDate = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        Map<Long, Long> statsMap = new HashMap<>();

        if (startDate != null) {
            List<ViewStatsDto> stats = statsClient.getStats(
                    LocalDateTime.of(2020, 1, 1, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    LocalDateTime.of(2025, 12, 31, 23, 59, 59)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), List.copyOf(eventUrisAndIds.keySet()), true);
            statsMap = stats.stream().collect(Collectors.toMap(
                    statsDto -> parseEventIdFromUrl(statsDto.getUri()),
                    ViewStatsDto::getHits
            ));
        }
        return statsMap;
    }*/

    protected Map<Long, Long> getViewsForEvents(List<Event> events) {
        if (events.isEmpty()) {
            return new HashMap<>();
        }

        // Формируем URI событий в формате /events/{eventId}
        Map<String, Long> eventUrisAndIds = events.stream()
                .collect(Collectors.toMap(
                        event -> String.format("/events/%s", event.getId()),
                        Event::getId
                ));

        // Определяем минимальную дату создания событий
        LocalDateTime startDate = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        // Если список событий пуст или дата не определена, возвращаем пустую карту
        if (startDate == null) {
            return new HashMap<>();
        }

        // Преобразуем даты в строки для запроса статистики
        String start = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Получаем статистику просмотров через StatsClient
        List<ViewStatsDto> stats = statsClient.getStats(start, end, new ArrayList<>(eventUrisAndIds.keySet()), true);

        // Преобразуем результат в карту: ID события -> количество просмотров
        return stats.stream()
                .collect(Collectors.toMap(
                        statsDto -> parseEventIdFromUrl(statsDto.getUri()),
                        ViewStatsDto::getHits
                ));
    }

    private Long parseEventIdFromUrl(String uri) {
        // Извлекаем ID события из URI (/events/{eventId})
        return Long.parseLong(uri.replaceAll(".*/(\\d+)", "$1"));
    }


    protected List<EventShortDto> createEventShortDto(Compilation compilation) {
        List<Event> allEvents = compilation.getEvents();
        Map<Long, Long> views = getViewsForEvents(allEvents);
        Map<Long, Long> confirmed = getConfirmedRequests(allEvents);
        return allEvents.stream()
                .map(event -> EventMapper.eventToEventShortDto(event,
                        views.getOrDefault(event.getId(), 0L),
                        confirmed.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }


/*    private Long parseEventIdFromUrl(String url) {
        String[] parts = url.split("/events/");
        if (parts.length == 2) {
            return Long.parseLong(parts[1]);
        }
        return -1L;
    }*/

    protected Map<Long, Long> getConfirmedRequests(List<Event> events) {
        if (events.isEmpty()) return Collections.emptyMap();
        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        List<CountDto> results = requestStorage.findByStatus(ids, CONFIRMED);
        return results.stream().collect(Collectors.toMap(CountDto::getEventId, CountDto::getCount));
    }


}
