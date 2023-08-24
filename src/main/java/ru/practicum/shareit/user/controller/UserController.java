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
    public UserDto createUser(
            @Valid @RequestBody UserDto userDto
    ) {
        log.info("Processing method create with params: userDto = {}", userDto);
        User user = UserMapper.toUser(userDto);
        User createdUser = userService.createUser(user);
        return UserMapper.toUserDto(createdUser);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(
            @PathVariable Long userId
    ) {
        log.info("Processing method getUserById with params: userId = {}", userId);
        return UserMapper.toUserDto(userService.findById(userId));
    }

    @PatchMapping("/{userId}")
    public UserDto update(
            @PathVariable Long userId,
            @RequestBody UserDto userDto
    ) {
        log.info("Processing method update with params: userId = {}, userDto = {}", userId, userDto);
        User user = UserMapper.toUser(userDto);
        User updateUser = userService.update(userId, user);

        return UserMapper.toUserDto(updateUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.remove(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Processing method getAllUsers");
        return UserMapper.toUserDtoList(userService.getAllUsers());
    }
}
