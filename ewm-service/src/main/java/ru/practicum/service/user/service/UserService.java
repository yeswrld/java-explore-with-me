package ru.practicum.service.user.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.service.user.dto.NewUserDto;
import ru.practicum.service.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(NewUserDto newUserDto, HttpServletRequest request);

    void delete(Long userId, HttpServletRequest request);

    List<UserDto> getAll(List<Long> idList, PageRequest pageRequest, HttpServletRequest request);
}
