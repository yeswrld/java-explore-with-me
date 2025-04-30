package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.stats.exception.DateErrorExcep;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.repository.HitStorage;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final HitStorage hitStorage;
    private final HitMapper hitMapper;

    @Override
    public HitDto saveHit(HitDto hitDto) {
        Hit hit = hitMapper.toHit(hitDto);
        System.out.println(hit);
        hitStorage.save(hit);
        return hitMapper.toHitDto(hit);
    }

    @Override
    public List<ViewStatsDto> findHits(LocalDateTime start, LocalDateTime end, List<String> uris,
                                       Boolean unique
    ) {
        if (start.isAfter(end)) {
            throw new DateErrorExcep("Ошибка даты.");
        }
        if (Boolean.TRUE.equals(unique)) {
            return hitStorage.findAllUnique(start, end, uris);
        } else {
            return hitStorage.findAll(start, end, uris);
        }
    }
}
