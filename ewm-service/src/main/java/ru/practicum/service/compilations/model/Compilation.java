package ru.practicum.service.compilations.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.service.events.model.Event;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "compilations")
@Getter
@Setter
@Builder
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    private Long id;
    @Column(name = "pinned")
    private Boolean pinned;
    @Column(name = "title")
    private String title;
    @ManyToMany
    @JoinTable(name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events;
}
