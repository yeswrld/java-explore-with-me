package ru.practicum.service.compilations.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.compilations.dto.CompilationDto;
import ru.practicum.service.compilations.service.PublicCompilationsServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {
    private final PublicCompilationsServiceImpl compilationsService;

    @GetMapping
    public List<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        log.info("PUBLIC ==>> Получение подборок событий");
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return compilationsService.getAllCompilations(pinned, pageRequest, request);
    }

    @GetMapping("/{compilationId}")
    public CompilationDto getCompilationById(@PathVariable Long compilationId, HttpServletRequest request) {
        log.info("PUBLIC ==>> Получение подборки событий с ИД = {}", compilationId);
        return compilationsService.getCompilationById(compilationId, request);
    }

}
