package ru.practicum.service.compilations.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.service.compilations.model.Compilation;

import java.util.List;

@Repository
public interface CompilationsStorage extends JpaRepository<Compilation, Long> {
    List<Compilation> findAllByPinned(Boolean pinned, PageRequest pageRequest);
}
