package ru.practicum.service.user.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.service.user.dto.NewUserDto;
import ru.practicum.service.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(NewUserDto newUserDto);

    void delete(Long userId);

    List<UserDto> getAll(List<Long> idList, PageRequest pageRequest);
}
