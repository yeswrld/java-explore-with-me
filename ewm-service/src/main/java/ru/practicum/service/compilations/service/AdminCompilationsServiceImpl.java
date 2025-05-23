package ru.practicum.service.compilations.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.client.StatsClient;
import ru.practicum.service.ViewService.BaseService;
import ru.practicum.service.compilations.dto.CompilationDto;
import ru.practicum.service.compilations.dto.CompilationUpdateDto;
import ru.practicum.service.compilations.dto.NewCompilationDto;
import ru.practicum.service.compilations.mapper.CompilationMapper;
import ru.practicum.service.compilations.model.Compilation;
import ru.practicum.service.compilations.storage.CompilationsStorage;
import ru.practicum.service.events.dto.EventShortDto;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.events.storage.EventStorage;
import ru.practicum.service.exception.NotFoundExcep;
import ru.practicum.service.requests.storage.RequestStorage;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class AdminCompilationsServiceImpl extends BaseService implements AdminCompilationsService {
    private final EventStorage eventStorage;
    private final CompilationsStorage compilationsStorage;
    private final StatsClient statsClient;
    @Value("${app}")
    private String appName;

    public AdminCompilationsServiceImpl(RequestStorage requestStorage, StatsClient statsClient, EventStorage eventStorage, CompilationsStorage compilationsStorage, StatsClient statsClient1) {
        super(requestStorage, statsClient);
        this.eventStorage = eventStorage;
        this.compilationsStorage = compilationsStorage;
        this.statsClient = statsClient1;
    }

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        List<Event> eventList = Collections.emptyList();
        if (newCompilationDto.getEvents() != null) {
            eventList = eventStorage.findAllById(newCompilationDto.getEvents());
        }
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, eventList);
        List<EventShortDto> eventShortDtos = createEventShortDto(compilation);
        return CompilationMapper.toCompilationDto(compilationsStorage.save(compilation), eventShortDtos);
    }

    @Override
    public void deleteCompilation(Long compilationId, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        Compilation compilation = compilationsStorage.findById(compilationId).orElseThrow(() -> new NotFoundExcep("Подборка с ИД = " + compilationId + " не найдена"));
        compilationsStorage.delete(compilation);
    }

    @Override
    public CompilationDto updateCompilation(Long compilationId, CompilationUpdateDto compilationUpdateDto, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        Compilation compilation = compilationsStorage.findById(compilationId).orElseThrow(()
                -> new NotFoundExcep("Компиляции не найдены"));
        List<Long> ids = compilationUpdateDto.getEvents();
        if (ids == null) {
            throw new NotFoundExcep("События не найдены");
        }
        List<Event> events = eventStorage.findAllById(ids);
        compilation.setEvents(events);
        if (compilationUpdateDto.getPinned() != null) {
            compilation.setPinned(compilationUpdateDto.getPinned());
        }
        if (compilationUpdateDto.getTitle() != null) {
            compilation.setTitle(compilationUpdateDto.getTitle());
        }
        return CompilationMapper.toCompilationDto(compilationsStorage.save(compilation), null);
    }

}
