package ru.practicum.service.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentDto {
    @NotNull
    private Long eventId;
    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(min = 1, max = 5000, message = "Длина комментария должна быть от 1 до 5000 символов")
    private String text;
}
