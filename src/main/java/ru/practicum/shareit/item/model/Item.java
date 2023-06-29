package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Item {
    Long id;
    String name;
    String description;
    User owner;
    ItemRequest request;
    Boolean available;


}
