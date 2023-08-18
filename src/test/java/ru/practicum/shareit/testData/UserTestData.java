package ru.practicum.shareit.testData;

import ru.practicum.shareit.user.model.User;

public class UserTestData {
    public static User getUserOne() {
        return new User(1L, "Name", "email@example.com");
    }

    public static User getUserTwo() {
        return new User(2L, "User", "email12@example.com");
    }

    public static User getUser() {
        return new User(3L, "UserName", "usernamee,ail@example.com");
    }
}
