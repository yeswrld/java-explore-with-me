package ru.practicum.service.user.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.service.user.model.User;

import java.util.List;

@Repository
public interface UserStorage extends JpaRepository<User, Long> {
    List<User> findAllByIdIn(List<Long> idList, PageRequest pageRequest);
}
