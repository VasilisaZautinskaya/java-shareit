package ru.practicum.shareit.user.mapper;


import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;


public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static List<UserDto> toUserDtoList(List<User> users) {
        List<UserDto> dtolist = new ArrayList<>();
        for (User user : users) {
            UserDto userDto = toUserDto(user);
            dtolist.add(userDto);
        }
        return dtolist;
    }
}
