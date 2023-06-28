package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public @ResponseBody Item createItem(@RequestBody Item item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.createItem(item, userId);
    }

    @GetMapping("/{itemId}")
    public @ResponseBody Item getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @PatchMapping("/{itemId}")
    public @ResponseBody Item update(@PathVariable Long itemId,
                                     @RequestBody Item item,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.update(itemId, item, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemService.remove(itemId);
    }

    @GetMapping
    public @ResponseBody List<Item> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public @ResponseBody List<Item> findItemByParams(@RequestParam String text) {
        return itemService.getItemsByText(text);
    }

}
