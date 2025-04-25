package ru.practicum.service.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.service.user.dto.NewUserDto;
import ru.practicum.service.user.dto.UserDto;
import ru.practicum.service.user.dto.UserShortDto;
import ru.practicum.service.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class UserMapper {
    public User toUser(NewUserDto newUserDto) {
        User user = new User();
        user.setName(newUserDto.getName());
        user.setEmail(newUserDto.getEmail());
        return user;
    }

    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static UserShortDto toShortUserDto(User user) {
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(user.getId());
        userShortDto.setName(user.getName());
        return userShortDto;
    }
}
