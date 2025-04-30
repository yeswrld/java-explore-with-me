package ru.practicum.service.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.service.location.dto.LocationDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdByUserDto {
    @Length(min = 20, max = 2000)
    private String annotation;
    private Long categoryId;
    @Length(min = 20, max = 7000)
    private String description;
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    @PositiveOrZero
    private Long participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    @Length(min = 3, max = 120)
    private String title;
}
