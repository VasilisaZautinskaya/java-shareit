package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Builder
@Data
public class ItemDto {
    Long id;
    String name;
    String description;
    User owner;
    ItemRequest request;
    boolean available;


}
