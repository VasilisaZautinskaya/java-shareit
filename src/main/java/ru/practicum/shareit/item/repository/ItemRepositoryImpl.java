package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
public class ItemRepositoryImpl implements ItemRepository {
    HashMap<Long, Item> items = new HashMap<>();
    long idSequence;

    @Override
    public Item update(Item item) {

        if (item.getId() == null) {
            return item;
        }

        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void remove(long itemId) {
        items.remove(itemId);
    }

    @Override
    public List<Item> getAll(Long userId) {
        ArrayList<Item> suitableItem = new ArrayList<>();
        for (Item item : items.values()
        ) {
            if (userId.equals(item.getOwner().getId())) {
                suitableItem.add(item);
            }

        }
        return suitableItem;
    }

    @Override
    public Item createItem(Item item) {
        if (item.getId() == null) {
            Long newId = generatedId();
            item.setId(newId);
        }

        items.put(item.getId(), item);
        return item;
    }


    @Override
    public long generatedId() {
        return ++idSequence;

    }

    @Override
    public List<Item> getItemByText(String text) {
        ArrayList<Item> searchedItems = new ArrayList<>();
        if (text.isEmpty() && text.length() == 0) {
            return new ArrayList<>();
        }
        for (Item item : items.values()
        ) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    && item.getAvailable() == true) {
                searchedItems.add(item);
            }
        }
        return searchedItems;
    }

    @Override
    public Item getItem(Long itemId) {
        return items.get(itemId);
    }
}
