package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
public class InMemoryItemRepository implements ItemRepository {
    HashMap<Long, Item> items = new HashMap<>();
    long idSequence;

    @Override
    public Item save(Item item) {

        if (item.getId() == null) {
            return item;
        }

        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(long itemId) {
        items.remove(itemId);
    }

    @Override
    public List<Item> findAll(Long userId) {
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



    public long generatedId() {
        return ++idSequence;

    }

    @Override
    public List<Item> findByText(String text) {
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
    public Item findById(Long itemId) {
        return items.get(itemId);
    }
}
