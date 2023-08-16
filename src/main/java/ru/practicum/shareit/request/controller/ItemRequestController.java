package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.Service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {

    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    private final ItemService itemService;


    @PostMapping
    public ItemRequestResponseDto create(
            @Valid @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        log.info("Processing method create with params: userId = {}, itemRequestDto = {}", userId, itemRequestDto);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(
                itemRequestDto,
                userService.findById(userId)
        );
        ItemRequest createdItemRequest = itemRequestService.createItemRequest(itemRequest);
        List<Item> items = itemService.findAllItemForRequest(itemRequest.getId());
        return ItemRequestMapper.toItemRequestResponseDto(createdItemRequest, items);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getById(
            @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        log.info("Processing method getById with params: userId = {}", userId);
        List<ItemRequest> itemRequests = itemRequestService.findAllItemRequest(userId);
        List<ItemRequestResponseDto> itemRequestResponseDtoList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests
        ) {
            List<Item> items = itemService.findAllItemForRequest(itemRequest.getId());
            ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper.toItemRequestResponseDto(itemRequest, items);
            itemRequestResponseDtoList.add(itemRequestResponseDto);
        }

        return itemRequestResponseDtoList;

    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllItemRequests(@RequestHeader(value = X_SHARER_USER_ID) Long userId,
                                                           @RequestParam(defaultValue = "0") int from,
                                                           @RequestParam(defaultValue = "10") int size) {
        log.info("Processing method getAllItemRequests with params: userId = {},  from = {}, size = {}", userId, from, size);
        List<ItemRequest> itemRequests = itemRequestService.findAllItemRequests(userId, from, size);
        List<ItemRequestResponseDto> itemRequestResponseDtoList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests
        ) {
            List<Item> items = itemService.findAllItemForRequest(itemRequest.getId());
            ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper.toItemRequestResponseDto(itemRequest, items);
            itemRequestResponseDtoList.add(itemRequestResponseDto);
        }

        return itemRequestResponseDtoList;
    }


    @GetMapping("/{requestId}")
    public ItemRequestResponseDto findById(@PathVariable Long requestId, @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Processing method findById with params: userId = {}, requestId = {}", userId, requestId);
        ItemRequest itemRequest = itemRequestService.findById(requestId, userId);
        List<Item> items = itemService.findAllItemForRequest(itemRequest.getId());
        return ItemRequestMapper.toItemRequestResponseDto(itemRequest, items);
    }
}
