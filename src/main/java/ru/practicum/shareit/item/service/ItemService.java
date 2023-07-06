package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;


@Service
@Slf4j
@AllArgsConstructor
public class ItemService {
    ItemRepository itemRepository;
    UserRepository inMemoryUserRepository;


    public Item createItem(Item item, Long userId) {
        if (userId == null) {
            log.info("UserId не может быть null");
            throw new ValidateException("UserId не может быть null");
        }
        User user = inMemoryUserRepository.findById(userId);
        if (user == null) {
            log.info("Такой пользователь не найден");
            throw new NotFoundException("Такой пользователь не найден");
        }
        if (item.getAvailable() == null) {
            log.info("Вещь должна быть доступна для бронирования");
            throw new ValidateException("Вещь должна быть доступна для бронирования");

        }
        if (item.getName().isEmpty()) {
            log.info("Имя не может быть null");
            throw new ValidateException("Имя не может быть null");
        }
        if (item.getDescription() == null) {
            log.info("Описание не может быть null");
            throw new ValidateException("Описание не может быть null");
        }
        item.setOwner(user);
        return itemRepository.createItem(item);

    }

    public Item update(Long itemId, Item item, Long userId) {
        Item oldItem = itemRepository.findById(itemId);

        if (userId == null) {
            log.info("UserId не может быть null");
            throw new ValidateException("UserId не может быть null");
        }

        if (!oldItem.getOwner().getId().equals(userId)) {
            log.info("Нельзя обновить вещь, принадлежащую другому пользователю");
            throw new ForbiddenException("Нельзя обновить вещь, принадлежащую другому пользователю");

        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getOwner() != null) {
            oldItem.setOwner(item.getOwner());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }

        return itemRepository.save(oldItem);
    }


    public void remove(long itemId) {

        itemRepository.delete(itemId);

    }


    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId);
    }

    public List<Item> getAll(Long userId) {
        return itemRepository.findAll(userId);
    }

    public List<Item> getItemsByText(String text) {

        return itemRepository.findByText(text);
    }
}
