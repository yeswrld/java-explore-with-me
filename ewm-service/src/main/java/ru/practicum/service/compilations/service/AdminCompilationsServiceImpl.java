package ru.practicum.service.compilations.service;

import org.springframework.stereotype.Service;
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

import java.util.Collections;
import java.util.List;

@Service
public class AdminCompilationsServiceImpl extends BaseService implements AdminCompilationsService {
    private final EventStorage eventStorage;
    private final CompilationsStorage compilationsStorage;

    public AdminCompilationsServiceImpl(RequestStorage requestStorage, StatsClient statsClient, EventStorage eventStorage, CompilationsStorage compilationsStorage) {
        super(requestStorage, statsClient);
        this.eventStorage = eventStorage;
        this.compilationsStorage = compilationsStorage;
    }

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        List<Event> eventList = Collections.emptyList();
        if (newCompilationDto.getEvents() != null) {
            eventList = eventStorage.findAllById(newCompilationDto.getEvents());
        }
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, eventList);
        List<EventShortDto> eventShortDtos = createEventShortDto(compilation);
        return CompilationMapper.toCompilationDto(compilationsStorage.save(compilation), eventShortDtos);
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        Compilation compilation = compilationsStorage.findById(compilationId).orElseThrow(() -> new NotFoundExcep("Подборка с ИД = " + compilationId + " не найдена"));
        compilationsStorage.delete(compilation);
    }

    @Override
    public CompilationDto updateCompilation(Long compilationId, CompilationUpdateDto compilationUpdateDto) {
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
