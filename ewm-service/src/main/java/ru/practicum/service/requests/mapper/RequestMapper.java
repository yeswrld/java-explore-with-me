package ru.practicum.service.requests.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.service.requests.dto.ParticipationRequestDto;
import ru.practicum.service.requests.model.Request;

import java.time.format.DateTimeFormatter;

@Component
public class RequestMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setId(request.getId());
        participationRequestDto.setCreated(request.getCreated().format(DATE_TIME_FORMATTER));
        participationRequestDto.setEvent(request.getEvent().getId());
        participationRequestDto.setRequester(request.getRequester().getId());
        participationRequestDto.setStatus(request.getStatus().toString());
        return participationRequestDto;
    }

    public static Request toRequest(ParticipationRequestDto participationRequestDto) {
        Request request = new Request();
        request.setId(participationRequestDto.getId());
        return request;
    }
}
