package ru.practicum.shareit.item.mapper;



import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;


public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        List<ItemDto> dtolist = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = toItemDto(item);
            dtolist.add(itemDto);
        }
        return dtolist;
    }
}
