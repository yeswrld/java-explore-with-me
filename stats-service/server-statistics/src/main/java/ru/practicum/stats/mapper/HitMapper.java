package ru.practicum.stats.mapper;

import org.mapstruct.Mapper;
import ru.practicum.HitDto;
import ru.practicum.stats.model.Hit;

@Mapper(componentModel = "spring")
public interface HitMapper {
    HitDto toHitDto(Hit hit);
    Hit toHit(HitDto hitDto);
}
