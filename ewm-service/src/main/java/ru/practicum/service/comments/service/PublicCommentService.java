package ru.practicum.service.comments.service;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.service.comments.dto.CommentDto;

import java.util.List;

public interface PublicCommentService {
    List<CommentDto> getAllComments(Long eventId, PageRequest pageRequest, HttpServletRequest request);

    CommentDto getCommentById(Long commentId, HttpServletRequest request);
}
