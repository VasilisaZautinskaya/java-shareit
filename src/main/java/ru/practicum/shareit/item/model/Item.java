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
    private Long id;
    private String name;
    private String description;
    private User owner;
    private ItemRequest request;
    private Boolean available;


}
