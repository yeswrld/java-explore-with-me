package ru.practicum.service.requests.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.service.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.service.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.service.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addRequest(Long userId, Long eventId, HttpServletRequest request);

    List<ParticipationRequestDto> getAllUserRequests(Long userId, HttpServletRequest request);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId, HttpServletRequest request);

    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId, HttpServletRequest request);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, HttpServletRequest request);
}
