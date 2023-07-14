package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.CommentMaper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final BookingService bookingService;

    @PostMapping
    public @ResponseBody ItemDto createItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {

        Item item = ItemMapper.toItem(itemDto);
        Item createdItem = itemService.createItem(item, userId);
        return ItemMapper.toItemDto(createdItem);
    }

    @GetMapping("/{itemId}")
    public @ResponseBody ItemWithBookingDto getItemById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId
    ) {

        Item item = itemService.getById(itemId);

        Booking nextItemBooking = null;
        Booking lastItemBooking = null;
        if (Objects.equals(item.getOwner().getId(), userId)) {
            nextItemBooking = bookingService.getNextBookingForItem(itemId);
            lastItemBooking = bookingService.getLastBookingForItem(itemId);
        }

        List<Comment> comments = itemService.findAllByItemId(itemId);
        ItemWithBookingDto getItemDto = ItemMapper.toItemWithBookingDto(item, lastItemBooking, nextItemBooking, comments);
        return getItemDto;
    }

    @PatchMapping("/{itemId}")
    public @ResponseBody ItemDto update(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        Item item = ItemMapper.toItem(itemDto);
        Item updatedItem = itemService.update(itemId, item, userId);
        return ItemMapper.toItemDto(updatedItem);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemService.deleteById(itemId);
    }

    @GetMapping
    public @ResponseBody List<ItemWithBookingDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemWithBookingDto> allItemDto = itemService.findAll(userId).stream()

                .map(item -> {
                            Booking lastItemBooking = null;
                            Booking nextItemBooking = null;
                            if (Objects.equals(item.getOwner().getId(), userId)) {
                                lastItemBooking = bookingService.getLastBookingForItem(item.getId());
                                nextItemBooking = bookingService.getNextBookingForItem(item.getId());
                            }
                            return ItemMapper.toItemWithBookingDto(item, lastItemBooking, nextItemBooking, Collections.emptyList());
                        }
                )
                .collect(Collectors.toList());
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
        if (commentDto.getText() == null || commentDto.getText().isEmpty()) {
            log.info("Текст комментария не может быть пустым");
            throw new ValidateException("");
        }

        Comment comment = CommentMaper.toComment(commentDto);
        Comment postComment = itemService.postComment(itemId, userId, comment);
        CommentDto postCommentDto = CommentMaper.toCommentDto(postComment);
        return postCommentDto;
    }

}
