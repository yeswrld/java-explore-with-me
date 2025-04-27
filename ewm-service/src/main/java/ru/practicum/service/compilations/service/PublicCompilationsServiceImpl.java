package ru.practicum.service.compilations.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.client.StatsClient;
import ru.practicum.service.ViewService.BaseService;
import ru.practicum.service.compilations.dto.CompilationDto;
import ru.practicum.service.compilations.mapper.CompilationMapper;
import ru.practicum.service.compilations.model.Compilation;
import ru.practicum.service.compilations.storage.CompilationsStorage;
import ru.practicum.service.events.dto.EventShortDto;
import ru.practicum.service.events.mapper.EventMapper;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.exception.NotFoundExcep;
import ru.practicum.service.requests.storage.RequestStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Service
public class PublicCompilationsServiceImpl extends BaseService implements PublicCompilationsService {
    private final CompilationsStorage compilationsStorage;
    private final StatsClient statsClient;
    @Value("${app}")
    private String appName;

    public PublicCompilationsServiceImpl(RequestStorage requestStorage, StatsClient statsClient, CompilationsStorage compilationsStorage, StatsClient statsClient1) {
        super(requestStorage, statsClient);
        this.compilationsStorage = compilationsStorage;
        this.statsClient = statsClient1;
    }


    @Override
    public List<CompilationDto> getAllCompilations(Boolean pined, PageRequest pageRequest, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        List<Compilation> compilationList;
        if (pined == null) {
            compilationList = compilationsStorage.findAll(pageRequest).toList();
        } else {
            compilationList = compilationsStorage.findAllByPinned(pined, pageRequest);
        }
        List<Event> allEvents = compilationList.stream().flatMap(compilation -> compilation.getEvents().stream()).toList();
        Map<Long, Long> views = getViewsForEvents(allEvents);
        Map<Long, Long> confirmed = getConfirmedRequests(allEvents);
        return CompilationMapper.compilationsListToCompilationDtoList(compilationList, confirmed, views);
    }

    @Override
    public CompilationDto getCompilationById(Long compilationId, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        Compilation compilation = compilationsStorage.findById(compilationId).orElseThrow(() -> new NotFoundExcep("Подборка с ИД = " + compilationId + " не найдена"));
        List<Event> allEvents = compilation.getEvents();
        Map<Long, Long> views = getViewsForEvents(allEvents);
        Map<Long, Long> confirmed = getConfirmedRequests(allEvents);
        List<EventShortDto> eventShortDtos = allEvents.stream()
                .map(event -> EventMapper.eventToEventShortDto(event,
                        views.getOrDefault(event.getId(), 0L),
                        confirmed.getOrDefault(event.getId(), 0L)))
                .toList();
        return CompilationMapper.toCompilationDto(compilation, eventShortDtos);
    }
}
