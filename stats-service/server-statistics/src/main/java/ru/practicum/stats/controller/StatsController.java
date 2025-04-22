package ru.practicum.stats.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.stats.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto saveHit(@RequestBody @Valid HitDto hitDto) {
        log.info("Обновление статистсики; app - {}, uri - {}, ip - {}, timestamp - {}", hitDto.getApp(),
                hitDto.getUri(), hitDto.getIp(), hitDto.getTimestamp());
        return statsService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(
            @RequestParam("start") @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime end,
            @RequestParam(required = false, value = "uris") List<String> uris,
            @RequestParam(required = false, value = "unique") boolean unique
    ) {
        log.info("Запрос получения статистики с параметрами: start {}, end {}, uris {}, unique {}", start, end, uris, unique);
        List<ViewStatsDto> statsDtoList = statsService.findHits(start, end, uris, unique);
        return statsDtoList;
    }
}
