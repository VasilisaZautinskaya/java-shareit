package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    public @ResponseBody ItemDto createItem(@RequestBody ItemDto itemDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {

        Item item = itemMapper.toItem(itemDto);
        Item createdItem = itemService.createItem(item, userId);
        ItemDto createdItemDto = itemMapper.toItemDto(createdItem);
        return createdItemDto;
    }

    @GetMapping("/{itemId}")
    public @ResponseBody ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId) {


        Item getItem = itemService.getItemById(itemId);
        ItemDto getItemDto = itemMapper.toItemDto(getItem);
        return getItemDto;
    }

    @PatchMapping("/{itemId}")
    public @ResponseBody ItemDto update(@PathVariable Long itemId,
                                        @RequestBody ItemDto itemDto,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = itemMapper.toItem(itemDto);
        Item updatedItem = itemService.update(itemId, item, userId);
        ItemDto updatedItemDto = itemMapper.toItemDto(updatedItem);

        return updatedItemDto;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemService.remove(itemId);
    }

    @GetMapping
    public @ResponseBody List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDto> allItemDto = itemMapper.toItemDtoList(itemService.getAll(userId));
        return allItemDto;
    }

    @GetMapping("/search")
    public @ResponseBody List<ItemDto> findItemByParams(@RequestParam String text) {
        List<ItemDto> items = itemMapper.toItemDtoList(itemService.getItemsByText(text));
        return items;
    }

}
