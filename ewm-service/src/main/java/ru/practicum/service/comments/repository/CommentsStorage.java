package ru.practicum.service.comments.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.service.comments.model.Comment;
import ru.practicum.service.events.model.State;

import java.util.List;

public interface CommentsStorage extends JpaRepository<Comment, Long> {
    List<Comment> findByUserId(Long userId, PageRequest pageRequest);

    List<Comment> findAllByEventIdAndState(Long eventId, State state, PageRequest pageRequest);
}
