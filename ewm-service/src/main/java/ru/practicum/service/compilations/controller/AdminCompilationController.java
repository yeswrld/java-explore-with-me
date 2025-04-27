package ru.practicum.service.compilations.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.compilations.dto.CompilationDto;
import ru.practicum.service.compilations.dto.CompilationUpdateDto;
import ru.practicum.service.compilations.dto.NewCompilationDto;
import ru.practicum.service.compilations.service.AdminCompilationsService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final AdminCompilationsService compilationsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto compilationDto, HttpServletRequest request) {
        log.info("ADMIN ==>> Добавление новой подборки событий {}", compilationDto);
        return compilationsService.addCompilation(compilationDto, request);
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Positive Long compilationId, HttpServletRequest request) {
        log.info("ADMIN ==>> Удаление подборки событий с ИД = {}", compilationId);
        compilationsService.deleteCompilation(compilationId, request);
    }

    @PatchMapping("/{compilationId}")
    public CompilationDto updateCompilation(@PathVariable Long compilationId, @RequestBody @Valid CompilationUpdateDto compilationUpdateDto, HttpServletRequest request) {
        log.info("ADMIN ==>> Обновление подборки событий с ИД = {}", compilationId);
        return compilationsService.updateCompilation(compilationId, compilationUpdateDto, request);
    }
}
