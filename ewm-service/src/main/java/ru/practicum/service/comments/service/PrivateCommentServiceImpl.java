package ru.practicum.service.comments.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.client.StatsClient;
import ru.practicum.service.comments.dto.CommentDto;
import ru.practicum.service.comments.dto.NewCommentDto;
import ru.practicum.service.comments.dto.UpdCommentDto;
import ru.practicum.service.comments.mapper.CommentMapper;
import ru.practicum.service.comments.model.Comment;
import ru.practicum.service.comments.repository.CommentsStorage;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.events.model.State;
import ru.practicum.service.events.storage.EventStorage;
import ru.practicum.service.exception.AccessExcep;
import ru.practicum.service.exception.NotFoundExcep;
import ru.practicum.service.user.dto.UserShortDto;
import ru.practicum.service.user.mapper.UserMapper;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.storage.UserStorage;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PrivateCommentServiceImpl implements PrivateCommentService {
    private final CommentsStorage commentsStorage;
    private final EventStorage eventStorage;
    private final UserStorage userStorage;
    private final StatsClient statsClient;
    @Value("${app}")
    private String appName;

    @Override
    public CommentDto addComment(NewCommentDto newCommentDto, Long userId, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        Event event = eventStorage.findById(newCommentDto.getEventId()).orElseThrow(() -> new NotFoundExcep("Эвент с ИД = " + newCommentDto.getEventId() + " не найден"));
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundExcep("Пользователь с ИД = " + userId + " не найден"));
        Comment comment = CommentMapper.toComment(newCommentDto, event, user);
        UserShortDto userShortDto = UserMapper.toShortUserDto(user);
        return CommentMapper.toCommentDto(commentsStorage.save(comment), userShortDto);
    }

    @Override
    public CommentDto updateComment(UpdCommentDto updCommentDto, Long userId, Long commentId, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundExcep("Пользователь с ИД = " + userId + " не найден"));
        UserShortDto userShortDto = UserMapper.toShortUserDto(user);
        Comment comment = commentsStorage.findById(commentId).orElseThrow(() -> new NotFoundExcep("Коммент с ИД = " + commentId + " не найден"));
        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessExcep("Пользователь с ИД = " + userId + " не автор комментария");
        }
        comment.setText(updCommentDto.getText());
        return CommentMapper.toCommentDto(commentsStorage.save(comment), userShortDto);
    }

    @Override
    public void deleteComment(Long userId, Long commentId, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundExcep("Пользователь с ИД = " + userId + " не найден"));
        Comment comment = commentsStorage.findById(commentId).orElseThrow(() -> new NotFoundExcep("Коммент с ИД = " + commentId + " не найден"));
        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessExcep("Пользователь с ИД = " + userId + " не автор комментария");
        }
        commentsStorage.delete(comment);
    }
}
