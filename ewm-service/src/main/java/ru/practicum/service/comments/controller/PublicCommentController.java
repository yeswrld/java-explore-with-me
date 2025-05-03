package ru.practicum.service.comments.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.comments.dto.CommentDto;
import ru.practicum.service.comments.service.PublicCommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
public class PublicCommentController {
    private final PublicCommentService publicCommentService;

    @GetMapping("/events/{eventId}")
    public List<CommentDto> getAllComments(@PathVariable Long eventId,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        log.info("PUBLIC ==>> Получение всех коментариев к эвенту с ИД = {}", eventId);
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());
        return publicCommentService.getAllComments(eventId, pageRequest, request);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable @Positive Long commentId, HttpServletRequest request) {
        log.info("PUBLIC ==>> Получение коментария с ИД = {}", commentId);
        return publicCommentService.getCommentById(commentId, request);
    }
}

