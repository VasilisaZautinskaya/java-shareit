package ru.practicum.shareit.testData;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemRequestTestData {
    public static ItemRequest getItemRequest(User user) {
        return new ItemRequest(20L, "New description", user, LocalDateTime.now());
     }

    public static ItemRequest getItemRequestTwo(User user) {
        return new ItemRequest(10L, "New description Two", user, LocalDateTime.now());
    }
}
