package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    protected Long id;
    @NotNull
    @NotBlank
    protected String name;
    @NotBlank
    protected String description;
    protected User owner;
    protected Long requestId;
    @NotNull
    protected Boolean available;
}
