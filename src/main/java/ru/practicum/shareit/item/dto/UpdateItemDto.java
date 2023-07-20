package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemDto {
    private Long id;
    private String name;
    private String description;
    private User owner;
    private ItemRequest request;
    private Boolean available;
}
