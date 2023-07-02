package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository {

    Item createItem(Item item);

    Item update(Item item);

    void remove(long itemId);

    List<Item> getAll(Long userId);

    long generatedId();

    Item getItem(Long itemId);

    List<Item> getItemByText(String text);
}
