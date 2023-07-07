package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository {

    Item createItem(Item item);

    Item save(Item item);

    void delete(long itemId);

    List<Item> findAll(Long userId);



    Item findById(Long itemId);

    List<Item> findByText(String text);
}
