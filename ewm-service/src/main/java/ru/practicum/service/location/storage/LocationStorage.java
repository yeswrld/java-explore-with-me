package ru.practicum.service.location.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.service.location.model.Location;

public interface LocationStorage extends JpaRepository<Location, Long> {
}
