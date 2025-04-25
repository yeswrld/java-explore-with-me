package ru.practicum.service.category.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.client.StatsClient;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.service.CategoryService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryControllerUser {
    private final CategoryService categoryService;
    private final StatsClient statsClient;

    @GetMapping("/{categoryId}")
    public CategoryDto getById(@PathVariable @Positive Long categoryId, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null,
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));
        log.info(" USER ==>> Поиск категории с ИД = {}", categoryId);
        return categoryService.getById(categoryId);
    }

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                    @RequestParam(defaultValue = "10") @Positive int size, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null,
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));
        log.info(" USER ==>> Получение всех категорий");
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return categoryService.getAll(pageRequest);
    }
}
