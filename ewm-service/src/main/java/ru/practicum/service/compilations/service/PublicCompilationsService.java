package ru.practicum.service.compilations.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.service.compilations.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationsService {
    List<CompilationDto> getAllCompilations(Boolean pined, PageRequest pageRequest, HttpServletRequest request);

    CompilationDto getCompilationById(Long compilationId, HttpServletRequest request);
}
