package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.mapper.CommentMaper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final BookingService bookingService;


    @PostMapping
    public @ResponseBody ItemDto createItem(@RequestBody ItemDto itemDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {

        Item item = ItemMapper.toItem(itemDto);
        Item createdItem = itemService.createItem(item, userId);
        ItemDto createdItemDto = ItemMapper.toItemDto(createdItem);
        return createdItemDto;
    }

    @GetMapping("/{itemId}")
    public @ResponseBody ItemWithBookingDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @PathVariable Long itemId) {

        Item getItem = itemService.findById(itemId);
        if (getItem == null) {
            throw new NotFoundException("Вещь не найдена");
        }

        Booking lastItemBooking = bookingService.getLastBookingForItem(itemId);
        Booking nextItemBooking = bookingService.getNextBookingForItem(itemId);
        List<Comment> comments = itemService.findAllByItemId(itemId);
        ItemWithBookingDto getItemDto = ItemMapper.toItemWithBookingDto(getItem, lastItemBooking, nextItemBooking, comments);
        return getItemDto;
    }

    @PatchMapping("/{itemId}")
    public @ResponseBody ItemDto update(@PathVariable Long itemId,
                                        @RequestBody ItemDto itemDto,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        Item updatedItem = itemService.update(itemId, item, userId);
        ItemDto updatedItemDto = ItemMapper.toItemDto(updatedItem);

        return updatedItemDto;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemService.deleteById(itemId);
    }

    @GetMapping
    public @ResponseBody List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDto> allItemDto = ItemMapper.toItemDtoList(itemService.findAll(userId));
        return allItemDto;
    }

    @GetMapping("/search")
    public @ResponseBody List<ItemDto> findItemByParams(@RequestParam String text) {
        List<ItemDto> items = ItemMapper.toItemDtoList(itemService.getItemsByText(text));
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestBody CommentDto commentDto) {


        Comment comment = CommentMaper.toComment(commentDto);
        Comment postComment = itemService.postComment(itemId, userId, comment);
        CommentDto postCommentDto = CommentMaper.toCommentDto(postComment);
        return postCommentDto;
    }

}
