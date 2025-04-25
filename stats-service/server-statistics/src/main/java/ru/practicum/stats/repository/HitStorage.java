package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ViewStatsDto;
import ru.practicum.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitStorage extends JpaRepository<Hit, Long> {
    @Query(" SELECT new ru.practicum.ViewStatsDto(eph.app, eph.uri, COUNT(eph.ip)) " +
            "FROM Hit eph WHERE eph.timestamp BETWEEN ?1 AND ?2 " +
            "AND (eph.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY eph.app, eph.uri " +
            "ORDER BY COUNT(eph.ip) DESC ")
    List<ViewStatsDto> findAll(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(" SELECT new ru.practicum.ViewStatsDto (eph.app, eph.uri, COUNT(DISTINCT eph.ip)) " +
            "FROM Hit eph WHERE eph.timestamp BETWEEN ?1 AND ?2 " +
            "AND (eph.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY eph.app, eph.uri " +
            "ORDER BY COUNT(DISTINCT eph.ip) DESC ")
    List<ViewStatsDto> findAllUnique(LocalDateTime start, LocalDateTime end, List<String> uris);
}
