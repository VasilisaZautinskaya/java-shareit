package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
public class ItemDto {
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotBlank
    private String description;
    private User owner;
    private ItemRequest request;

    @NotNull
    private Boolean available;


}
