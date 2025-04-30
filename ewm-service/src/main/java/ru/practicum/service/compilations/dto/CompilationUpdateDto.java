package ru.practicum.service.compilations.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationUpdateDto {
    private List<Long> events = List.of();
    private Boolean pinned;
    @Size(max = 50)
    private String title;
}
