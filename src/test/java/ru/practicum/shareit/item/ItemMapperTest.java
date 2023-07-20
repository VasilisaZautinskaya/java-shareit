package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.testData.*;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

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
        User user = UserTestData.getUserOne();
        ItemRequest request = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItem(request);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        Assertions.assertThat(item.getId()).isEqualTo(itemDto.getId());
        Assertions.assertThat(item.getName()).isEqualTo(itemDto.getName());
        Assertions.assertThat(item.getDescription()).isEqualTo(itemDto.getDescription());
        Assertions.assertThat(item.getAvailable()).isEqualTo(itemDto.getAvailable());
        Assertions.assertThat(item.getRequest().getId()).isEqualTo(itemDto.getRequestId());
    }

    @Test
    public void testToItemDtoNull() {
        ItemDto itemDto = ItemMapper.toItemDto(null);
        Assertions.assertThat(itemDto).isNull();
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

    @Test
    public void testToItemDtoList() {
        List<Item> items = new ArrayList<>();
        ItemRequest itemRequest = new ItemRequest();
        User user = UserTestData.getUserOne();
        Item itemOne = ItemTestData.getItemOne(itemRequest, user);
        Item itemTwo = ItemTestData.getItemTwo(itemRequest, user);
        items.add(itemOne);
        items.add(itemTwo);

        List<ItemDto> dtoList = ItemMapper.toItemDtoList(items);

        Assertions.assertThat(items.size()).isEqualTo(2);
        Assertions.assertThat(items.size()).isEqualTo(dtoList.size());
    }

    @Test
    public void testToItemWithBookingDto() {
        ItemRequest itemRequest = new ItemRequest();
        User user = UserTestData.getUserOne();
        User userComment = UserTestData.getUserTwo();
        Item item = ItemTestData.getItemOne(itemRequest, user);
        Booking lastBooking = BookingTestData.getBookingOne(userComment, item);
        Booking nextBooking = BookingTestData.getBookingTwo(userComment, item);
        List<Comment> comments = new ArrayList<>();
        Comment commentOne = CommentTestData.getCommentOne(userComment, item);
        Comment commentTwo = CommentTestData.getCommentTwo(userComment, item);
        comments.add(commentOne);
        comments.add(commentTwo);

        ItemWithBookingDto itemWithBookingDto = ItemMapper.toItemWithBookingDto(item, lastBooking, nextBooking, comments);

        Assertions.assertThat(comments.size()).isEqualTo(2);
        Assertions.assertThat(itemWithBookingDto.getId()).isEqualTo(item.getId());
        Assertions.assertThat(itemWithBookingDto.getDescription()).isEqualTo(item.getDescription());
        Assertions.assertThat(itemWithBookingDto.getName()).isEqualTo(item.getName());
        Assertions.assertThat(itemWithBookingDto.getAvailable()).isEqualTo(item.getAvailable());
        Assertions.assertThat(itemWithBookingDto.getLastBooking().getId()).isEqualTo(lastBooking.getId());
        Assertions.assertThat(itemWithBookingDto.getNextBooking().getId()).isEqualTo(nextBooking.getId());

    }

}

