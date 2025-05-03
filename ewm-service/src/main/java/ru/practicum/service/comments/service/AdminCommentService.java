package ru.practicum.service.comments.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.service.comments.dto.CommentDto;
import ru.practicum.service.comments.dto.FullCommentDto;
import ru.practicum.service.comments.dto.UpdatedStatusDto;

import java.util.List;

public interface AdminCommentService {
    void deleteComment(Long commentId, HttpServletRequest request);

    FullCommentDto updateCommentStatus(Long commentId, UpdatedStatusDto updatedStatusDto, HttpServletRequest request);

    List<CommentDto> getUserComments(Long userId, PageRequest pageRequest, HttpServletRequest request);

}
