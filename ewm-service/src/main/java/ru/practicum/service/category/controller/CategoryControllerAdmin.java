package ru.practicum.service.category.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.client.StatsClient;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.dto.NewCategoryDto;
import ru.practicum.service.category.service.CategoryService;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryControllerAdmin {
    private final CategoryService categoryService;
    private final StatsClient statsClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto add(@RequestBody @Valid NewCategoryDto newCategoryDto, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null,
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));
        log.info("ADMIN ==>> Добавление категории с именем {}", newCategoryDto.getName());
        return categoryService.add(newCategoryDto);
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto update(@RequestBody @Valid NewCategoryDto newCategoryDto, @PathVariable @Positive Long categoryId, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null,
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));
        log.info("ADMIN ==>> Обновление категории с ИД = {} на {}", categoryId, newCategoryDto.getName());
        return categoryService.update(categoryId, newCategoryDto);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long categoryId, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null,
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));
        log.info("ADMIN ==>> Удаленеие категории с ИД = {}", categoryId);
        categoryService.delete(categoryId);
    }
}
