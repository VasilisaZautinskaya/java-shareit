package ru.practicum.shareit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemRequestMapperTest {
    @Test
    public void testToRequest() {
        // Prepare test data
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
}
