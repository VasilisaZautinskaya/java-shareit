package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    public @ResponseBody UserDto getUserById(
            @PathVariable Long userId
    ) {
        return UserMapper.toUserDto(userService.findById(userId));
    }

    @PatchMapping("/{userId}")
    public @ResponseBody UserDto update(
            @PathVariable Long userId,
            @RequestBody UserDto userDto
    ) {

        User user = UserMapper.toUser(userDto);
        User updateUser = userService.update(userId, user);

        return UserMapper.toUserDto(updateUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.remove(userId);
    }

    @GetMapping
    public @ResponseBody List<UserDto> getAllUsers() {
        return UserMapper.toUserDtoList(userService.getAllUsers());
    }
}
