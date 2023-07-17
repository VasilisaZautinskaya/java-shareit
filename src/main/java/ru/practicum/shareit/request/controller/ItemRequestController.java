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

    private final ItemRequestService itemRequestService;
    private final UserService userService;

    private final ItemService itemService;


    @PostMapping
    public @ResponseBody ItemRequestResponseDto createRequest(
            @Valid @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(
                itemRequestDto,
                userService.getById(userId)
        );
        ItemRequest createdItemRequest = itemRequestService.createItemRequest(itemRequest, userId);
        List<Item> items = itemService.findAllItemForRequest(itemRequest.getId());
        return ItemRequestMapper.toItemRequestResponseDto(createdItemRequest, items);
    }

    @GetMapping
    public @ResponseBody List<ItemRequestResponseDto> getItemsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
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
    public @ResponseBody List<ItemRequestResponseDto> getAllItemRequests(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                                                         @RequestParam(defaultValue = "0", required = false) int from,
                                                                         @RequestParam(defaultValue = "10", required = false) int size) {
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
    public @ResponseBody ItemRequestResponseDto findById(@PathVariable Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemRequest itemRequest = itemRequestService.findById(requestId, userId);
        List<Item> items = itemService.findAllItemForRequest(itemRequest.getId());
        return ItemRequestMapper.toItemRequestResponseDto(itemRequest, items);
    }
}
