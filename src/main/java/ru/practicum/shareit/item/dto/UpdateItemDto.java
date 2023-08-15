package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemDto {
    private Long id;
    private String name;
    private String description;
    private UserDto owner;
    private ItemRequestDto request;
    private Boolean available;
}
