package ru.practicum.service.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.service.exception.NotFoundExcep;
import ru.practicum.service.user.dto.NewUserDto;
import ru.practicum.service.user.dto.UserDto;
import ru.practicum.service.user.mapper.UserMapper;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto add(NewUserDto newUserDto) {
        User newUser = userStorage.save(userMapper.toUser(newUserDto));
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public void delete(Long userId) {
        User userForDelete = userStorage.findById(userId).orElseThrow(() -> new NotFoundExcep("Пользователь с ИД = " + userId + " не найден"));
        userStorage.delete(userForDelete);
    }

    @Override
    public List<UserDto> getAll(List<Long> idList, PageRequest pageRequest) {
        List<User> userList;
        if (idList.isEmpty()) {
            userList = userStorage.findAll(pageRequest).stream().toList();
        } else {
            userList = userStorage.findAllByIdIn(idList, pageRequest);
        }
        return userList.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}
