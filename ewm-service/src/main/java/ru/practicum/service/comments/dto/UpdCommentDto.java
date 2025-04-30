package ru.practicum.service.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdCommentDto {
    @NotBlank
    @Size(max = 5000)
    String text;
}
