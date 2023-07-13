package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository inMemoryUserRepository;

    private final JpaBookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final BookingService bookingService;


    public Item createItem(Item item, Long userId) {

        if (userId == null) {
            log.info("UserId не может быть null");
            throw new ValidateException("UserId не может быть null");
        }
        if (item.getName().isEmpty()) {
            log.info("Имя не может быть null");
            throw new ValidateException("Имя не может быть null");
        }
        if (item.getDescription() == null) {
            log.info("Описание не может быть null");
            throw new ValidateException("Описание не может быть null");
        }
        if (item.getAvailable() == null) {
            log.info("Вещь должна быть доступна для бронирования");
            throw new ValidateException("Вещь должна быть доступна для бронирования");
        }
        User user = inMemoryUserRepository.findById(userId);
        if (user == null) {
            log.info("Такой пользователь не найден");
            throw new NotFoundException("Такой пользователь не найден");
        }

        item.setOwner(user);
        return itemRepository.save(item);

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


    public void deleteById(long itemId) {
        itemRepository.deleteById(itemId);
    }


    public Item findById(Long itemId) {

        return itemRepository.findById(itemId);
    }

    public List<Item> findAll(Long userId) {
        return itemRepository.findAll(userId);
    }

    public List<Item> getItemsByText(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.getSearch(text);
    }

    public Comment postComment(Long itemId, Long userId, Comment comment) {
        if (userId == null) {
            log.info("UserId не может быть null");
            throw new NotFoundException("UserId не может быть null");
        }

        User user = inMemoryUserRepository.findById(userId);
        if (user == null) {
            log.info("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }

        Item item = findById(itemId);
        if (itemId == null) {
            log.error("Вещь с таким id  не найдена");
            throw new NotFoundException("Вещь с таким id  не найдена");
        }
        if (!bookingService.isUserBookedItem(user, item)) {
            log.info("Вы не можете оставить комментарий, так как не бронировали эту вещь");
            throw new ValidateException("Вы не можете оставить комментарий, так как не бронировали эту вещь");
        }

        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);

    }

    public List<Comment> findAllByItemId(Long itemId) {
        return commentRepository.findAllByItemId(itemId);
    }

}
