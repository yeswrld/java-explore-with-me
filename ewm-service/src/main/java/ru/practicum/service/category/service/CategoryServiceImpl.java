package ru.practicum.service.category.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.client.StatsClient;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.dto.NewCategoryDto;
import ru.practicum.service.category.mapper.CategoryMapper;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.category.storage.CategoryStorage;
import ru.practicum.service.exception.NotFoundExcep;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryStorage categoryStorage;
    private final StatsClient statsClient;
    @Value("${app}")
    private String appName;

    @Override
    public CategoryDto add(NewCategoryDto newCategoryDto, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        Category newCategory = categoryStorage.save(categoryMapper.toCategory(newCategoryDto));
        return CategoryMapper.toCategoryDto(newCategory);
    }

    @Override
    public void delete(Long categoryId, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        Category category = categoryStorage.findById(categoryId).orElseThrow(() -> new NotFoundExcep("Категория с ИД = " + categoryId + " не найдена"));
        categoryStorage.delete(category);
    }

    @Override
    public CategoryDto update(Long categoryId, NewCategoryDto newCategoryDto, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        Category category = categoryStorage.findById(categoryId).orElseThrow(() -> new NotFoundExcep("Категория с ИД = " + categoryId + " не найдена"));
        category.setName(newCategoryDto.getName());
        categoryStorage.save(category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAll(PageRequest pageRequest, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        return categoryStorage.findAll(pageRequest).toList().stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long categoryId, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        Category category = categoryStorage.findById(categoryId).orElseThrow(() -> new NotFoundExcep("Категория с ИД = " + categoryId + " не найдена"));
        return CategoryMapper.toCategoryDto(category);
    }
}
