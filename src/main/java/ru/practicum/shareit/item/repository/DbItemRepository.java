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
    public Item save(Item item) {
        return jpaItemRepository.save(item);
    }

    @Override
    public void deleteById(long itemId) {
        jpaItemRepository.deleteById(itemId);
    }

    @Override
    public List<Item> findAll(Long userId) {
        return jpaItemRepository.findAllByOwnerIdOrderById(userId);
    }

    @Override
    public List<Item> findAll() {
        return jpaItemRepository.findAll();
    }

    @Override
    public Item findById(Long itemId) {

        return jpaItemRepository.findById(itemId).orElse(null);
    }

    @Override
    public List<Item> getSearch(String text) {

        return jpaItemRepository.getSearch(text);
    }

    @Override
    public List<Item> findAllByRequestId(Long requestId) {
        return jpaItemRepository.findAllByRequestId(requestId);
    }
}
