package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    public ItemRequest createItemRequest(ItemRequest itemRequest, Long userId) {
        if (userId == null) {
            log.info("");
            throw new ValidateException("");
        }
        itemRequest.setRequestor(userService.getById(userId));
        return itemRequestRepository.save(itemRequest);
    }


    public List<ItemRequest> findAllItemRequest(Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            log.info("Такой пользователь не найден");
            throw new NotFoundException("Такой пользователь не найден");
        }
        return itemRequestRepository.findAllByRequestorId(userId);
    }

    public List<Item> findAllItem(Long requestId) {
        return itemRequestRepository.findAllByRequestId(requestId);
    }

    /*   public List<ItemRequest> getAllItemRequests(Long userId, int from, int size) {
           if (from < 0 || size < 1) {
               log.info("");
               throw new ValidateException("");
           }
           if (userService.getById(userId) == null) {
               log.info("");
               throw new NotFoundException("");
           }
           PageRequest page = PageRequest.of(from, size);

          return Page<ItemRequest> itemRequestList = itemRequestRepository.findByOrderByCreatedDesc(page);


       }*/
    public ItemRequest findById(Long requestId, Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new NotFoundException("Такой запрос не найден");
        }
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);
        if (itemRequest.isEmpty()) {
            throw new NotFoundException("Такой запрос не найден");
        }

        return itemRequest.orElse(null);
    }
}




