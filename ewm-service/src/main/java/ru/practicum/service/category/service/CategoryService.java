package ru.practicum.service.category.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto add(NewCategoryDto newCategoryDto, HttpServletRequest request);

    void delete(Long categoryId, HttpServletRequest request);

    CategoryDto update(Long categoryId, NewCategoryDto newCategoryDto, HttpServletRequest request);

    List<CategoryDto> getAll(PageRequest pageRequest, HttpServletRequest request);

    CategoryDto getById(Long categoryId, HttpServletRequest request);
}
