package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.repository.HitStorage;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final HitStorage hitStorage;

    @Autowired
    private final HitMapper hitMapper;

    @Override
    public HitDto saveHit(HitDto hitDto) {
        Hit hit = hitMapper.toHit(hitDto);
        System.out.println(hit);
        hitStorage.save(hit);
        return hitMapper.toHitDto(hit);
    }

    @Override
    public List<ViewStatsDto> findHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (Boolean.TRUE.equals(unique)) {
            return hitStorage.findUniqueStats(start, end, uris);
        } else {
            return hitStorage.findAllStats(start, end, uris);
        }
    }
}
