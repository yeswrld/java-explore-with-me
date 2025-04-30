package ru.practicum.service.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {
    @Size(min = 2, max = 250)
    @NotBlank
    private String name;
    @Email
    @Size(min = 6, max = 254)
    @NotBlank
    private String email;
}
