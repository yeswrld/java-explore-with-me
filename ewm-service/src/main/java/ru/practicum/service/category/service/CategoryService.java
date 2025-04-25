package ru.practicum.service.category.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto add(NewCategoryDto newCategoryDto);

    void delete(Long categoryId);

    CategoryDto update(Long categoryId, NewCategoryDto newCategoryDto);

    List<CategoryDto> getAll(PageRequest pageRequest);

    CategoryDto getById(Long categoryId);
}
