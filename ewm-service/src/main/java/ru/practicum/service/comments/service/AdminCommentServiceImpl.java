package ru.practicum.service.comments.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.client.StatsClient;
import ru.practicum.service.comments.dto.CommentDto;
import ru.practicum.service.comments.dto.FullCommentDto;
import ru.practicum.service.comments.dto.UpdatedStatusDto;
import ru.practicum.service.comments.mapper.CommentMapper;
import ru.practicum.service.comments.model.Comment;
import ru.practicum.service.comments.repository.CommentsStorage;
import ru.practicum.service.exception.NotFoundExcep;
import ru.practicum.service.exception.ViolationExcep;
import ru.practicum.service.user.mapper.UserMapper;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminCommentServiceImpl implements AdminCommentService {
    private static final Logger log = LoggerFactory.getLogger(AdminCommentServiceImpl.class);
    private final CommentsStorage commentsStorage;
    private final UserStorage userStorage;
    private final StatsClient statsClient;
    @Value("${app}")
    private String appName;

    @Override
    public void deleteComment(Long commentId, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        Comment comment = commentsStorage.findById(commentId).orElseThrow(() -> new NotFoundExcep("Коммент с ИД = " + commentId + " не найден"));
        commentsStorage.delete(comment);
    }

    @Override
    public FullCommentDto updateCommentStatus(Long commentId, UpdatedStatusDto updatedStatusDto, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        Comment comment = commentsStorage.findById(commentId).orElseThrow(() -> new NotFoundExcep("Коммент с ИД = " + commentId + " не найден"));
        log.info("Статус до обновления {}", comment.getState());
        if (updatedStatusDto.getState() != null && comment.getState() == updatedStatusDto.getState()) {
            throw new ViolationExcep("Статус события уже " + comment.getState());
        }
        comment.setState(updatedStatusDto.getState());
        log.info("Статус после обновления {}", comment.getState());
        return CommentMapper.toFullComment(commentsStorage.save(comment), UserMapper.toShortUserDto(comment.getUser()));

    }

    @Override
    public List<CommentDto> getUserComments(Long userId, PageRequest pageRequest, HttpServletRequest request) {
        statsClient.addHit(new HitDto(null, appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundExcep("Пользователь с ИД = " + userId + " не найден"));
        List<Comment> comments = commentsStorage.findByUserId(userId, pageRequest);
        return comments.stream().map(comment -> CommentMapper.toCommentDto(comment, UserMapper.toShortUserDto(comment.getUser()))).toList();
    }
}
