package ru.practicum.service.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.dto.NewCategoryDto;
import ru.practicum.service.category.mapper.CategoryMapper;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.category.storage.CategoryStorage;
import ru.practicum.service.exception.NotFoundExcep;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryStorage categoryStorage;

    @Override
    public CategoryDto add(NewCategoryDto newCategoryDto) {
        Category newCategory = categoryStorage.save(categoryMapper.toCategory(newCategoryDto));
        return CategoryMapper.toCategoryDto(newCategory);
    }

    @Override
    public void delete(Long categoryId) {
        Category category = categoryStorage.findById(categoryId).orElseThrow(() -> new NotFoundExcep("Категория с ИД = " + categoryId + " не найдена"));
        categoryStorage.delete(category);
    }

    @Override
    public CategoryDto update(Long categoryId, NewCategoryDto newCategoryDto) {
        Category category = categoryStorage.findById(categoryId).orElseThrow(() -> new NotFoundExcep("Категория с ИД = " + categoryId + " не найдена"));
        category.setName(newCategoryDto.getName());
        categoryStorage.save(category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAll(PageRequest pageRequest) {
        return categoryStorage.findAll(pageRequest).toList().stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long categoryId) {
        Category category = categoryStorage.findById(categoryId).orElseThrow(() -> new NotFoundExcep("Категория с ИД = " + categoryId + " не найдена"));
        return CategoryMapper.toCategoryDto(category);
    }
}
