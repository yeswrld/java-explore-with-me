package ru.practicum.service.comments.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.comments.dto.CommentDto;
import ru.practicum.service.comments.dto.FullCommentDto;
import ru.practicum.service.comments.dto.UpdatedStatusDto;
import ru.practicum.service.comments.service.AdminCommentService;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Slf4j
public class AdminCommentController {
    private final AdminCommentService commentService;

    @GetMapping("/users/{userId}")
    public List<CommentDto> getUserComments(@PathVariable @Positive Long userId,
                                            @RequestParam(defaultValue = "0") @Positive int from,
                                            @RequestParam(defaultValue = "10") @Positive int size, HttpServletRequest request) {
        log.info("ADMIN ==>> Получение всех комментариев пользователя с ИД = {}", userId);
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());
        return commentService.getUserComments(userId, pageRequest, request);
    }

    @PatchMapping("/{commentId}")
    public FullCommentDto updateCommentStatus(@RequestBody UpdatedStatusDto updatedStatusDto, @PathVariable @Positive Long commentId, HttpServletRequest request) {
        log.info("ADMIN ==>> Обновление статуса комментария с ИД = {} на статус {}", commentId, updatedStatusDto.getState());
        return commentService.updateCommentStatus(commentId, updatedStatusDto, request);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long commentId, HttpServletRequest request) {
        log.info("ADMIN ==>> Удаление комментария с ИД = {}", commentId);
        commentService.deleteComment(commentId, request);
    }

}
