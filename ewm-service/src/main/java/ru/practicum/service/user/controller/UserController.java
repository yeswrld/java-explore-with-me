package ru.practicum.service.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.user.dto.NewUserDto;
import ru.practicum.service.user.dto.UserDto;
import ru.practicum.service.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid NewUserDto newUserDto, HttpServletRequest request) {
        log.info("ADMIN ==>> Добавление нового пользователя {}", newUserDto.getName());
        return userService.add(newUserDto, request);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(defaultValue = "") List<Long> ids,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                  @RequestParam(defaultValue = "10") @Positive int size, HttpServletRequest request) {
        log.info("ADMIN ==>> Получение информации о пользователях");
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return userService.getAll(ids, pageRequest, request);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long userId, HttpServletRequest request) {
        log.info("ADMIN ==>> Удаляем пользователя с ИД = {}", userId);
        userService.delete(userId, request);
    }
}
