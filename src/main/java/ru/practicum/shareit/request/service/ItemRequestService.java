package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.Service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;


    public ItemRequest createItemRequest(ItemRequest itemRequest) {
        if (itemRequest.getCreated() == null) {
            itemRequest.setCreated(LocalDateTime.now());
        }

        return itemRequestRepository.save(itemRequest);
    }


    public List<ItemRequest> findAllItemRequest(Long userId) {
        userService.findById(userId);
        return itemRequestRepository.findAllByRequestorId(userId);
    }


    public List<ItemRequest> findAllItemRequests(Long userId, int from, int size) {
        if (from < 0 || size < 1) {
            log.info("Неверный номер страницы");
            throw new ValidateException("Неверный номер страницы");
        }
        userService.findById(userId);

        PageRequest page = PageRequest.of(from, size);

        Page<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId, page);

        return itemRequestList.toList();

    }

    public ItemRequest findByIdSilent(Long requestId) {
        return itemRequestRepository.findById(requestId).orElse(null);
    }

    public ItemRequest findById(Long requestId, Long userId) {
        userService.findById(userId);
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);
        if (itemRequest.isEmpty()) {
            log.info("Такой запрос не найден");
            throw new NotFoundException("Такой запрос не найден");
        }

        return itemRequest.orElse(null);
    }
}




