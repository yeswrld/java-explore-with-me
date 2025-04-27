package ru.practicum.service.compilations.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.service.compilations.dto.CompilationDto;
import ru.practicum.service.compilations.dto.CompilationUpdateDto;
import ru.practicum.service.compilations.dto.NewCompilationDto;

public interface AdminCompilationsService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto, HttpServletRequest request);

    void deleteCompilation(Long compilationId, HttpServletRequest request);

    CompilationDto updateCompilation(Long compilationId, CompilationUpdateDto compilationUpdateDto, HttpServletRequest request);
}
