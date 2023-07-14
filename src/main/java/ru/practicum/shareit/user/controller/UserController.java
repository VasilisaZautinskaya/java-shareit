package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public @ResponseBody UserDto createUser(
            @Valid @RequestBody UserDto userDto
    ) {
        User user = UserMapper.toUser(userDto);
        User createdUser = userService.createUser(user);
        return UserMapper.toUserDto(createdUser);
    }

    @GetMapping("/{userId}")
    public @ResponseBody UserDto getUserById(@PathVariable Long userId) {
        User getdUser = userService.getById(userId);
        if (getdUser == null) {
            throw new NotFoundException("Не найден пользователь");
        }
        UserDto getdUserDto = UserMapper.toUserDto(getdUser);

        return getdUserDto;
    }

    @PatchMapping("/{userId}")
    public @ResponseBody UserDto update(@PathVariable Long userId,
                                        @RequestBody UserDto userDto) {

        User user = UserMapper.toUser(userDto);
        User updateUser = userService.update(userId, user);
        UserDto updateUserDto = UserMapper.toUserDto(updateUser);

        return updateUserDto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.remove(userId);
    }

    @GetMapping
    public @ResponseBody List<UserDto> getAllUsers() {
        List<UserDto> allUserDto = UserMapper.toUserDtoList(userService.getAllUsers());
        return allUserDto;
    }

}
