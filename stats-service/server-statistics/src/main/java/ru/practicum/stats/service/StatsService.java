package ru.practicum.stats.service;

import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    HitDto saveHit(HitDto hitDto);

    List<ViewStatsDto> findHits(LocalDateTime start, LocalDateTime end, List<String> uri, Boolean unique);
}
