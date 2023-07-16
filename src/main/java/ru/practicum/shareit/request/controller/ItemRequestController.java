package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.Service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final UserService userService;


    @PostMapping
    public @ResponseBody ItemRequestDto createRequest(
            @Valid @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(
                itemRequestDto,
                userService.getById(userId)
        );
        ItemRequest createdItemRequest = itemRequestService.createItemRequest(itemRequest, userId);
        return ItemRequestMapper.toItemRequestDto(createdItemRequest);
    }

    @GetMapping
    public @ResponseBody List<ItemRequestDto> getItemsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        List<ItemRequest> itemRequests = itemRequestService.findAllItemRequest(userId);
        return ItemRequestMapper.toItemRequestDtoList(itemRequests);

    }

    /*  @GetMapping("/all")
      public @ResponseBody List<ItemRequestDto> getAllItemRequests(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                                                   @RequestParam(defaultValue = "0", required = false) int from,
                                                                   @RequestParam(defaultValue = "10", required = false) int size) {
          if (userId == null) {
              log.info("");
              throw new ValidateException("");
          }

          return ItemRequestMapper.toItemRequestDtoList(itemRequestService.getAllItemRequests(userId, from, size));
      }

     */
    @GetMapping("/{requestId}")
    public @ResponseBody ItemRequestDto findById(@PathVariable Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemRequest itemRequest = itemRequestService.findById(requestId, userId);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }
}
