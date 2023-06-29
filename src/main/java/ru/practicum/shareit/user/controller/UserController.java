package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;


    @PostMapping
    public @ResponseBody UserDto createUser(@RequestBody UserDto userDto) {

        User user = userMapper.toUser(userDto);
        User createdUser = userService.createUser(user);
        UserDto createdUserDto = userMapper.toUserDto(createdUser);

        return createdUserDto;
    }

    @GetMapping("/{userId}")
    public @ResponseBody UserDto getUserById(@PathVariable Long userId) {

        User getdUser = userService.getUserById(userId);
        UserDto getdUserDto = userMapper.toUserDto(getdUser);

        return getdUserDto;
    }

    @PatchMapping("/{userId}")
    public @ResponseBody UserDto update(@PathVariable Long userId,
                                        @RequestBody UserDto userDto) {

        User user = userMapper.toUser(userDto);
        User updateUser = userService.update(userId, user);
        UserDto updateUserDto = userMapper.toUserDto(updateUser);

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
