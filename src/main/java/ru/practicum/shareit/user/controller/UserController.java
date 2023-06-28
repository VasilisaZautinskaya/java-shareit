package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping
    public @ResponseBody User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{userId}")
    public @ResponseBody User getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public @ResponseBody User update(@PathVariable Long userId,
                                     @RequestBody User user) {
        return userService.update(userId,user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.remove(userId);
    }

    @GetMapping
    public @ResponseBody List<User> getAllUsers() {
        return userService.getAllUsers();
    }

}
