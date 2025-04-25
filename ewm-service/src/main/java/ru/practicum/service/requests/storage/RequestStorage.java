package ru.practicum.service.requests.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.service.events.dto.CountDto;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.requests.model.Request;
import ru.practicum.service.requests.model.RequestCount;
import ru.practicum.service.requests.model.Status;

import java.util.List;

@Repository
public interface RequestStorage extends JpaRepository<Request, Long> {
    @Query("SELECT new ru.practicum.service.events.dto.CountDto(r.event.id, COUNT(r.id)) " +
            "FROM Request AS r " +
            "WHERE r.status = :status AND r.event.id IN :ids " +
            "GROUP BY r.event.id")
    List<CountDto> findByStatus(@Param("ids") List<Long> ids,
                                @Param("status") Status status);

    @Query("SELECT new ru.practicum.service.requests.model.RequestCount(r.event.id, COUNT(r.id)) " +
            "FROM Request r " +
            "WHERE r.event.id IN :eventIds AND r.status = :status " +
            "GROUP BY r.event.id")
    List<RequestCount> countByEventIdInAndStatus(
            @Param("eventIds") List<Long> eventIds,
            @Param("status") Status status);

    @Query("SELECT r FROM Request r " +
            "WHERE r.event.id = :eventId " +
            "AND r.event.initiator.id = :userId")
    List<Request> findByEventIdAndInitiatorId(
            @Param("eventId") Long eventId,
            @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Request r SET r.status = CASE WHEN (:confirmedCount < :limit) THEN 'CONFIRMED' ELSE 'REJECTED' END " +
            "WHERE r.id IN :requestIds AND r.status = 'PENDING'")
    void updateRequestsStatusBulk(
            @Param("requestIds") List<Long> requestIds,
            @Param("confirmedCount") long confirmedCount,
            @Param("limit") int limit);

    List<Request> findAllByRequesterIdAndEventId(Long requesterId, Long eventId);

    Long countByEventIdAndStatus(Long eventId, Status status);

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByEvent(Event event);

}
