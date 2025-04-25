package ru.practicum.service.requests.service;

import ru.practicum.service.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.service.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.service.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllUserRequests(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
