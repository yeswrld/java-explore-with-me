package ru.practicum.service.comments.dto;

import lombok.Data;
import ru.practicum.service.events.model.State;

@Data
public class UpdatedStatusDto {
    State state;
}
