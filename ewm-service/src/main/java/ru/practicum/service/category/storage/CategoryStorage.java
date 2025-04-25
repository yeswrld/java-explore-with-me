package ru.practicum.service.category.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.service.category.model.Category;

@Repository
public interface CategoryStorage extends JpaRepository<Category, Long> {

}
