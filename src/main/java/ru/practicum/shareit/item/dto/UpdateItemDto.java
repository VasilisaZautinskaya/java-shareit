package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;


@Builder
@Data
public class UpdateItemDto {
    private Long id;
    private String name;
    private String description;
    private User owner;
    private ItemRequest request;
    private Boolean available;
}
