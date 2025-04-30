package ru.practicum.service.events.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.events.model.State;
import ru.practicum.service.location.dto.LocationDto;
import ru.practicum.service.user.dto.UserShortDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    private Long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private Boolean paid;
    private String eventDate;
    private UserShortDto initiator;
    private String description;
    private Long participantLimit;
    private State state;
    private String createdOn;
    private LocationDto location;
    private Boolean requestModeration;
    private Long confirmedRequests;
    private String publishedOn;
    private Long views;
}
