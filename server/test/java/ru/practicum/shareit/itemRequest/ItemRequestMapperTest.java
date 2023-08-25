package ru.practicum.shareit.itemRequest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapperTest {
    @Test
    public void testToRequest() {
        Long requestId = 1L;
        String description = "Request Description";
        LocalDateTime created = LocalDateTime.now();
        User user = new User(3L, "Name", "email@example.com");
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(requestId);
        itemRequestDto.setDescription(description);
        itemRequestDto.setCreated(created);

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);

        Assertions.assertThat(requestId).isEqualTo(itemRequest.getId());
        Assertions.assertThat(description).isEqualTo(itemRequest.getDescription());
        Assertions.assertThat(created).isEqualTo(itemRequest.getCreated());
    }

    @Test
    public void testToDto() {
        User user = new User(3L, "Name", "email@example.com");
        Long requestId = 1L;
        String description = "Request Description";
        LocalDateTime created = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription(description);
        itemRequest.setCreated(created);
        itemRequest.setRequestor(user);

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest);


        Assertions.assertThat(requestId).isEqualTo(dto.getId());
        Assertions.assertThat(description).isEqualTo(dto.getDescription());
        Assertions.assertThat(created).isEqualTo(dto.getCreated());

    }

    @Test
    public void testToItemRequestResponseDto() {
        ItemRequest itemRequest = new ItemRequest();
        User user = new User(3L, "Name", "email@example.com");
        List<Item> items = new ArrayList<>();
        Item itemOne = new Item(1L, "Item One", "Item One", user, itemRequest, true);
        Item itemTwo = new Item(2L, "Item Two", "Item Two", user, itemRequest, true);
        items.add(itemOne);
        items.add(itemTwo);
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper.toItemRequestResponseDto(itemRequest, items);

        Assertions.assertThat(itemRequest.getId()).isEqualTo(itemRequestResponseDto.getId());
        Assertions.assertThat(itemRequest.getDescription()).isEqualTo(itemRequestResponseDto.getDescription());
        Assertions.assertThat(itemRequest.getRequestor().getId()).isEqualTo(itemRequestResponseDto.getRequestorId());
        Assertions.assertThat(itemRequest.getCreated()).isEqualTo(itemRequestResponseDto.getCreated());
        Assertions.assertThat(items.size()).isEqualTo(2);
    }
}
