package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingService bookingService;

    private final ItemRequestService itemRequestService;


    public Item createItem(Item item, Long ownerId, Long requestId) {
        item.setOwner(userService.findById(ownerId));
        if (requestId != null) {
            item.setRequest(itemRequestService.findByIdSilent(requestId));
        }
        return itemRepository.save(item);
    }

    public Item update(Long itemId, Item item, Long userId) {

        Item oldItem = itemRepository.findById(itemId);
        validateThatUserIsOwner(userId, oldItem);

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

    private void validateThatUserIsOwner(Long userId, Item oldItem) {
        if (!oldItem.getOwner().getId().equals(userId)) {
            log.info("Нельзя обновить вещь, принадлежащую другому пользователю");
            throw new ForbiddenException("Нельзя обновить вещь, принадлежащую другому пользователю");
        }
    }


    public void deleteById(long itemId) {
        itemRepository.deleteById(itemId);
    }


    public Item findById(Long itemId) {

        Item item = itemRepository.findById(itemId);
        if (item == null) {
            log.info("Вещь не найдена");
            throw new NotFoundException("Вещь не найдена");
        }
        return item;
    }

    public List<Item> findAll(Long userId) {
        return itemRepository.findAll(userId);
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public List<Item> findItemsByText(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.getSearch(text);
    }

    public Comment postComment(Long itemId, Long authorId, Comment comment) {

        User author = userService.findById(authorId);
        Item item = findById(itemId);

        validateThatUserHadBookedItem(author, item);

        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    private void validateThatUserHadBookedItem(User user, Item item) {
        if (!bookingService.isUserBookedItem(user, item)) {
            log.info("Вы не можете оставить комментарий, так как не бронировали эту вещь");
            throw new ValidateException("Вы не можете оставить комментарий, так как не бронировали эту вещь");
        }
    }

    public List<Comment> findAllByItemId(Long itemId) {
        return commentRepository.findAllByItemId(itemId);
    }

    public List<Item> findAllItemForRequest(Long requestId) {
        return itemRepository.findAllByRequestId(requestId);
    }
}
