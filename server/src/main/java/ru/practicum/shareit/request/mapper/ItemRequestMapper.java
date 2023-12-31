package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestor(user)
                .created(itemRequestDto.getCreated())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestor().getId())
                .created(itemRequest.getCreated())
                .build();
    }


    public static ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest, List<Item> items) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestor().getId())
                .items(ItemMapper.toItemDtoList(items))
                .created(itemRequest.getCreated())
                .build();
    }

    public static List<ItemRequestResponseDto> toItemRequestResponseDtoList(List<ItemRequest> itemRequests, List<Item> allItems) {
        List<ItemRequestResponseDto> itemRequestResponseDtoList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests
        ) {
            List<Item> items = allItems.stream()
                    .filter(item -> item.getRequest() != null && item.getRequest().getId().equals(itemRequest.getId()))
                    .collect(Collectors.toList());
            ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper.toItemRequestResponseDto(itemRequest, items);
            itemRequestResponseDtoList.add(itemRequestResponseDto);
        }
        return itemRequestResponseDtoList;
    }
}
