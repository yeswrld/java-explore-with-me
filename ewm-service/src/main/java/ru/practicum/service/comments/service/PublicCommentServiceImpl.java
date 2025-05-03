package ru.practicum.service.comments.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.client.StatsClient;
import ru.practicum.service.comments.dto.CommentDto;
import ru.practicum.service.comments.mapper.CommentMapper;
import ru.practicum.service.comments.model.Comment;
import ru.practicum.service.comments.repository.CommentsStorage;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.events.model.State;
import ru.practicum.service.events.storage.EventStorage;
import ru.practicum.service.exception.NotFoundExcep;
import ru.practicum.service.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicCommentServiceImpl implements PublicCommentService {
    private final EventStorage eventStorage;
    private final CommentsStorage commentsStorage;
    private final StatsClient statsClient;
    @Value("${app}")
    private String appName;

    @Override
    public List<CommentDto> getAllComments(Long eventId, PageRequest pageRequest, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        Event event = eventStorage.findById(eventId).orElseThrow(() -> new NotFoundExcep("Эвент c ИД = " + eventId + " не найден"));
        List<Comment> comments = commentsStorage.findAllByEventIdAndState(eventId, State.PUBLISHED, pageRequest);
        return comments.stream().map(comment -> CommentMapper.toCommentDto(comment, UserMapper.toShortUserDto(comment.getUser()))).toList();
    }

    @Override
    public CommentDto getCommentById(Long commentId, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        Comment comment = commentsStorage.findById(commentId).orElseThrow(() -> new NotFoundExcep("Коммент с ИД = " + commentId + " не найден"));
        return CommentMapper.toCommentDto(comment, UserMapper.toShortUserDto(comment.getUser()));
    }
}
