package ru.practicum.service.comments.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.comments.dto.CommentDto;
import ru.practicum.service.comments.dto.NewCommentDto;
import ru.practicum.service.comments.dto.UpdCommentDto;
import ru.practicum.service.comments.service.PrivateCommentService;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Slf4j
public class PrivateCommentController {
    private final PrivateCommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@RequestBody @Validated NewCommentDto newCommentDto, @PathVariable @Positive Long userId, HttpServletRequest request) {
        log.info("USER ==>> Добавление комментария {}, пользователем с ИД = {}", newCommentDto.getText(), userId);
        return commentService.addComment(newCommentDto, userId, request);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@RequestBody @Validated UpdCommentDto updCommentDto, @PathVariable @Positive Long userId, @PathVariable Long commentId, HttpServletRequest request) {
        log.info("USER ==>> Обновление комментария c ИД = {}, пользователем с ИД = {}", commentId, userId);
        return commentService.updateComment(updCommentDto, userId, commentId, request);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long userId, @PathVariable Long commentId, HttpServletRequest request) {
        log.info("USER ==>> Удаление комментария c ИД = {}, пользователем с ИД = {}", commentId, userId);
        commentService.deleteComment(userId, commentId, request);
    }
}
