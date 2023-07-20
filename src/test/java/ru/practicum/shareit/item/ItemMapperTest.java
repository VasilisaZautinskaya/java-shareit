package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemMapperTest {
    @Test
    public void testToItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("New Item");
        itemDto.setDescription("New item description");
        itemDto.setAvailable(true);


        Item item = ItemMapper.toItem(itemDto);


        Assertions.assertThat(itemDto.getId()).isEqualTo(item.getId());
        Assertions.assertThat(itemDto.getName()).isEqualTo(item.getName());
        Assertions.assertThat(itemDto.getDescription()).isEqualTo(item.getDescription());
        Assertions.assertThat(itemDto.getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    public void testToItemDto() {
        User user = new User();
        Item item = new Item();
        item.setId(2L);
        item.setName("Item 2");
        item.setDescription("Description 2");
        item.setAvailable(false);
        ItemRequest request = new ItemRequest(20L, "New description", user, LocalDateTime.now());
        item.setRequest(request);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        Assertions.assertThat(item.getId()).isEqualTo(itemDto.getId());
        Assertions.assertThat(item.getName()).isEqualTo(itemDto.getName());
        Assertions.assertThat(item.getDescription()).isEqualTo(itemDto.getDescription());
        Assertions.assertThat(item.getAvailable()).isEqualTo(itemDto.getAvailable());
        Assertions.assertThat(item.getRequest().getId()).isEqualTo(itemDto.getRequestId());
    }

    @Test
    public void testToUpdateItem() {
        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setId(1L);
        updateItemDto.setName("Update");
        updateItemDto.setDescription("Item update");
        updateItemDto.setAvailable(true);

        Item item = ItemMapper.toItem(updateItemDto);


        Assertions.assertThat(updateItemDto.getId()).isEqualTo(item.getId());
        Assertions.assertThat(updateItemDto.getName()).isEqualTo(item.getName());
        Assertions.assertThat(updateItemDto.getDescription()).isEqualTo(item.getDescription());
        Assertions.assertThat(updateItemDto.getAvailable()).isEqualTo(item.getAvailable());
    }
}

