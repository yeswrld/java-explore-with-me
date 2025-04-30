package ru.practicum.service.requests.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.requests.dto.ParticipationRequestDto;
import ru.practicum.service.requests.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable @Positive Long userId, @RequestParam @Positive Long eventId, HttpServletRequest request) {
        log.info("REQUESTS ==>> Добавление запроса на участие в эвенте {}, от пользователя с ИД = {}", eventId, userId);
        return requestService.addRequest(userId, eventId, request);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAllRequests(@PathVariable @Positive Long userId, HttpServletRequest request) {
        log.info("REQUESTS ==>> Получение всех запросов пользователя с ИД = {}", userId);
        return requestService.getAllUserRequests(userId, request);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @Positive Long userId, @PathVariable @Positive Long requestId, HttpServletRequest request) {
        log.info("REQUESTS ==>> Отмента запроса на участие с ИД = {} от пользователя с ИД = {}", requestId, userId);
        return requestService.cancelRequest(userId, requestId, request);
    }
}
