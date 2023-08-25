package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
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

    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final BookingService bookingService;

    @PostMapping
    public ItemDto createItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        log.info("Processing method create with params: userId = {}, itemDto = {}", userId, itemDto);
        Item item = ItemMapper.toItem(itemDto);
        Long requestId = itemDto.getRequestId();
        Item createdItem = itemService.createItem(item, userId, requestId);
        return ItemMapper.toItemDto(createdItem);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto getItemById(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId
    ) {
        log.info("Processing method getItemById with params: userId = {}, itemId = {}", userId, itemId);
        Item item = itemService.findById(itemId);
        List<Comment> comments = itemService.findAllByItemId(itemId);
        return toItemWithBookingDto(userId, item, comments);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateItemDto itemDto,
            @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        log.info("Processing method update with params: userId = {}, itemId = {}, updateItemDto = {}", userId, itemId, itemDto);
        Item item = ItemMapper.toItem(itemDto);
        Item updatedItem = itemService.update(itemId, item, userId);
        return ItemMapper.toItemDto(updatedItem);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemService.deleteById(itemId);
    }

    @GetMapping
    public List<ItemWithBookingDto> getAllItems(
            @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        log.info("Processing method getAllItems with params: userId = {}", userId);
        List<ItemWithBookingDto> allItemDto = itemService.findAll(userId).stream()
                .map(item -> toItemWithBookingDto(userId, item, Collections.emptyList()))
                .collect(Collectors.toList());
        return allItemDto;
    }

    private ItemWithBookingDto toItemWithBookingDto(Long userId, Item item, List<Comment> comments) {
        Booking lastItemBooking = null;
        Booking nextItemBooking = null;
        if (Objects.equals(item.getOwner().getId(), userId)) {
            lastItemBooking = bookingService.getLastBookingForItem(item.getId());
            nextItemBooking = bookingService.getNextBookingForItem(item.getId());
        }
        return ItemMapper.toItemWithBookingDto(item, lastItemBooking, nextItemBooking, comments);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemByParams(
            @RequestParam String text
    ) {
        log.info("Processing method findItemByParams with params: text = {}", text);
        return ItemMapper.toItemDtoList(itemService.findItemsByText(text));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(
            @PathVariable Long itemId,
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody CommentDto commentDto
    ) {
        log.info("Processing method postComment with params: itemId= {}, userId = {}, commentDto = {}", itemId, userId, commentDto);
        Comment comment = CommentMapper.toComment(commentDto);
        Comment postComment = itemService.postComment(itemId, userId, comment);
        return CommentMapper.toCommentDto(postComment);
    }

}
