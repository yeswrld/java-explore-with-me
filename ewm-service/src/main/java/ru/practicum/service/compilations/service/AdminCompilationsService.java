package ru.practicum.service.compilations.service;

import ru.practicum.service.compilations.dto.CompilationDto;
import ru.practicum.service.compilations.dto.CompilationUpdateDto;
import ru.practicum.service.compilations.dto.NewCompilationDto;

public interface AdminCompilationsService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compilationId);

    CompilationDto updateCompilation(Long compilationId, CompilationUpdateDto compilationUpdateDto);
}
