package ru.practicum.service.compilations.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.service.compilations.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationsService {
    List<CompilationDto> getAllCompilations(Boolean pined, PageRequest pageRequest);

    CompilationDto getCompilationById(Long compilationId);
}
