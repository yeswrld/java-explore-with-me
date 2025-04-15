package ru.practicum.stats.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.HitDto;
import ru.practicum.stats.model.Hit;

@Component
//@Mapper(componentModel = "spring")
public class HitMapper {
    public HitDto toHitDto(Hit hit) {
        return new HitDto(
                hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp()
        );
    }

    public Hit toHit(HitDto hitDto) {
        return new Hit(
                hitDto.getId(),
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                hitDto.getTimestamp()
        );
    }
}
