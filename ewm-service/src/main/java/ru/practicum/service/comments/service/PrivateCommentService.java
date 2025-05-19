package ru.practicum.service.comments.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.service.comments.dto.CommentDto;
import ru.practicum.service.comments.dto.NewCommentDto;
import ru.practicum.service.comments.dto.UpdCommentDto;

public interface PrivateCommentService {
    CommentDto addComment(NewCommentDto newCommentDto, Long userId, HttpServletRequest request);

    CommentDto updateComment(UpdCommentDto updCommentDto, Long userId, Long commentId, HttpServletRequest request);

    void deleteComment(Long userId, Long commentId, HttpServletRequest request);
}
