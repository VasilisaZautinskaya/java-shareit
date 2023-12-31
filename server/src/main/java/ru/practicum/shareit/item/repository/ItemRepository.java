package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository {

    Item save(Item item);

    void deleteById(long itemId);

    List<Item> findAll(Long userId);

    List<Item> findAll();

    Item findById(Long itemId);

    List<Item> getSearch(String text);

    List<Item> findAllByRequestId(Long requestId);



}
