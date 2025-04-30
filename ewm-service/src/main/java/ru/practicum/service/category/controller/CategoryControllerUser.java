package ru.practicum.service.category.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryControllerUser {
    private final CategoryService categoryService;

    @GetMapping("/{categoryId}")
    public CategoryDto getById(@PathVariable @Positive Long categoryId, HttpServletRequest request) {
        log.info(" USER ==>> Поиск категории с ИД = {}", categoryId);
        return categoryService.getById(categoryId, request);
    }

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                    @RequestParam(defaultValue = "10") @Positive int size, HttpServletRequest request) {
        log.info(" USER ==>> Получение всех категорий");
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return categoryService.getAll(pageRequest, request);
    }
}
