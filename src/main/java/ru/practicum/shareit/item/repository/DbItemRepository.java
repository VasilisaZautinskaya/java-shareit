package ru.practicum.shareit.item.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
@Primary
public class DbItemRepository implements ItemRepository {
    @Autowired
    JpaItemRepository jpaItemRepository;

    @Override
    public Item createItem(Item item) {
        return null;
    }

    @Override
    public Item save(Item item) {
        return jpaItemRepository.save(item);
    }

    @Override
    public void delete(long itemId) {
        jpaItemRepository.deleteById(itemId);
    }

    @Override
    public List<Item> findAll(Long userId) {
        return findAll(userId);
    }


    @Override
    public Item findById(Long itemId) {
        return findById(itemId);
    }

    @Override
    public List<Item> findByText(String text) {
        return findByText(text);
    }
}
