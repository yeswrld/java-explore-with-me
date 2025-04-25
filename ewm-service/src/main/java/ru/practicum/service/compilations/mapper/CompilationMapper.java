package ru.practicum.service.compilations.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.service.compilations.dto.CompilationDto;
import ru.practicum.service.compilations.dto.NewCompilationDto;
import ru.practicum.service.compilations.model.Compilation;
import ru.practicum.service.events.dto.EventShortDto;
import ru.practicum.service.events.mapper.EventMapper;
import ru.practicum.service.events.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventShortDtos) {
        return new CompilationDto(compilation.getId(), eventShortDtos, compilation.getPinned(), compilation.getTitle());
    }

    public static Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> eventList) {
        return new Compilation(null, newCompilationDto.getPinned(), newCompilationDto.getTitle(), eventList);
    }

    public static List<CompilationDto> compilationsListToCompilationDtoList(
            List<Compilation> compilations,
            Map<Long, Long> confirmed,
            Map<Long, Long> views) {

        List<CompilationDto> result = new ArrayList<>();
        for (Compilation compilation : compilations) {
            CompilationDto compilationDto = new CompilationDto(compilation.getId(),
                    EventMapper.eventListToEventShortDtoList(new ArrayList<>(compilation.getEvents()), views, confirmed),
                    compilation.getPinned(),
                    compilation.getTitle());
            result.add(compilationDto);
        }
        return result;
    }
}
