package ru.practicum.shareit.testData;

import ru.practicum.shareit.user.model.User;

public class UserTestData {
    public static User getUserOne() {
        User user = new User(1L, "Name", "email@example.com");
        return user;
    }

    public static User getUserTwo() {
        User userComment = new User(3L, "User", "email12@example.com");
        return userComment;
    }
}
