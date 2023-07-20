package ru.practicum.shareit.testData;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemTestData {
    public static Item getItem(ItemRequest request) {
        Item item = new Item();
        item.setId(2L);
        item.setName("Item 2");
        item.setDescription("Description 2");
        item.setAvailable(false);
        item.setRequest(request);
        return item;
    }

    public static Item getItemTwo(ItemRequest itemRequest, User owner) {
        return new Item(2L, "Item Two", "Item Two", owner, itemRequest, true);
    }

    public static Item getItemOne(ItemRequest itemRequest, User owner) {
        Item itemOne = new Item(1L, "Item One", "Item One", owner, itemRequest, true);
        return itemOne;
    }
}
