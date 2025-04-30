package ru.practicum.service.requests.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestCount {
    private Long eventId;
    private Long count;
}