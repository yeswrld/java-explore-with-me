package ru.practicum.service.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.events.model.State;
import ru.practicum.service.events.storage.EventStorage;
import ru.practicum.service.exception.NotFoundExcep;
import ru.practicum.service.exception.ViolationExcep;
import ru.practicum.service.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.service.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.service.requests.dto.ParticipationRequestDto;
import ru.practicum.service.requests.mapper.RequestMapper;
import ru.practicum.service.requests.model.Request;
import ru.practicum.service.requests.model.Status;
import ru.practicum.service.requests.storage.RequestStorage;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.service.requests.model.Status.*;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserStorage userStorage;
    private final EventStorage eventStorage;
    private final RequestStorage requestStorage;

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        if (!requestStorage.findAllByRequesterIdAndEventId(userId, eventId).isEmpty()) {
            throw new ViolationExcep("Запрос уже существует");
        }
        Event event = eventStorage.findById(eventId).orElseThrow(() -> new NotFoundExcep("Эвент с ИД = " + eventId + " не найден"));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ViolationExcep("Эвент не опубликован");
        }
        if (event.getParticipantLimit() != 0 && requestStorage.countByEventIdAndStatus(eventId, CONFIRMED) >= event.getParticipantLimit()) {
            throw new ViolationExcep("У эвента максимальное кол-во участников");
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new ViolationExcep("Инициатор не может добавить запрос на участие в своём эвенте");
        }
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundExcep("Пользователь с ИД = " + userId + " не найден"));
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);
        request.setStatus(PENDING);
        Status status;
        if (event.getRequestModeration()) {
            status = PENDING;
        } else {
            status = CONFIRMED;
        }
        if (event.getParticipantLimit() == 0) {
            request.setStatus(CONFIRMED);
        } else {
            request.setStatus(status);
        }
        return RequestMapper.toParticipationRequestDto(requestStorage.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getAllUserRequests(Long userId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundExcep("Пользователь с ИД = " + userId + " не найден"));
        return requestStorage.findAllByRequesterId(userId).stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundExcep("Пользователь с ИД = " + userId + " не найден"));
        Request request = requestStorage.findById(requestId).orElseThrow(() -> new NotFoundExcep("Запрос на участие с ИД = " + requestId + " не найден"));
        request.setStatus(CANCELED);
        return RequestMapper.toParticipationRequestDto(requestStorage.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundExcep("Пользователь с ИД = " + userId + " не найден"));
        Event event = eventStorage.findById(eventId).orElseThrow(() -> new NotFoundExcep("Эвент с ИД = " + userId + " не найден"));
        List<Request> requests = requestStorage.findByEventIdAndInitiatorId(eventId, userId);
        if (!requests.isEmpty()) {
            return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
        } else
            return new ArrayList<>();
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        // 1. Проверка существования пользователя и события
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundExcep("Пользователь с ИД = " + userId + " не найден"));
        Event event = eventStorage.findById(eventId)
                .orElseThrow(() -> new NotFoundExcep("Эвент с ИД = " + eventId + " не найден"));

        // 2. Получаем запросы для обновления
        List<Request> requests = requestStorage.findAllById(updateRequest.getRequestIds());

        // 3. Проверяем, что все запросы в статусе PENDING
        if (requests.stream().anyMatch(request -> !request.getStatus().equals(Status.PENDING))) {
            throw new ViolationExcep("Статус можно изменить только у запросов в состоянии ожидания");
        }

        // 4. Обработка в зависимости от статуса
        if (updateRequest.getStatus().equals(REJECTED)) {
            return processRejection(requests);
        } else if (updateRequest.getStatus().equals(CONFIRMED)) {
            return processConfirmation(event, requests);
        }

        return new EventRequestStatusUpdateResult(List.of(), List.of());
    }

    private EventRequestStatusUpdateResult processRejection(List<Request> requests) {
        requests.forEach(request -> request.setStatus(REJECTED));
        requestStorage.saveAll(requests);

        List<ParticipationRequestDto> rejected = requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();

        return new EventRequestStatusUpdateResult(List.of(), rejected);
    }

    private EventRequestStatusUpdateResult processConfirmation(Event event, List<Request> requests) {
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            requests.forEach(request -> request.setStatus(CONFIRMED));
            requestStorage.saveAll(requests);

            List<ParticipationRequestDto> confirmed = requests.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .toList();

            return new EventRequestStatusUpdateResult(confirmed, List.of());
        }

        long confirmedCount = requestStorage.countByEventIdAndStatus(event.getId(), CONFIRMED);
        int availableSlots = (int) (event.getParticipantLimit() - confirmedCount);

        if (availableSlots <= 0) {
            throw new ViolationExcep("Лимит участников для события достигнут");
        }

        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        for (Request request : requests) {
            if (confirmed.size() < availableSlots) {
                request.setStatus(CONFIRMED);
                confirmed.add(request);
            } else {
                request.setStatus(REJECTED);
                rejected.add(request);
            }
        }

        requestStorage.saveAll(requests);

        return new EventRequestStatusUpdateResult(
                confirmed.stream()
                        .map(RequestMapper::toParticipationRequestDto)
                        .toList(),
                rejected.stream()
                        .map(RequestMapper::toParticipationRequestDto)
                        .toList()
        );
    }
}
