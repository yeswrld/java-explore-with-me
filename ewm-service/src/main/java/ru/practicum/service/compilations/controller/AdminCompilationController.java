package ru.practicum.service.compilations.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.client.StatsClient;
import ru.practicum.service.compilations.dto.CompilationDto;
import ru.practicum.service.compilations.dto.CompilationUpdateDto;
import ru.practicum.service.compilations.dto.NewCompilationDto;
import ru.practicum.service.compilations.service.AdminCompilationsService;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final AdminCompilationsService compilationsService;
    private final StatsClient statsClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto compilationDto, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null,
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));
        log.info("ADMIN ==>> Добавление новой подборки событий {}", compilationDto);
        return compilationsService.addCompilation(compilationDto);
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Positive Long compilationId, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null,
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));
        log.info("ADMIN ==>> Удаление подборки событий с ИД = {}", compilationId);
        compilationsService.deleteCompilation(compilationId);
    }

    @PatchMapping("/{compilationId}")
    public CompilationDto updateCompilation(@PathVariable Long compilationId, @RequestBody @Valid CompilationUpdateDto compilationUpdateDto, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null,
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));
        log.info("ADMIN ==>> Обновление подборки событий с ИД = {}", compilationId);
        return compilationsService.updateCompilation(compilationId, compilationUpdateDto);
    }
}
