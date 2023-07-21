package ru.practicum.shareit.itemRequest;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.testData.CommentTestData;
import ru.practicum.shareit.testData.ItemRequestTestData;
import ru.practicum.shareit.testData.ItemTestData;
import ru.practicum.shareit.testData.UserTestData;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemRequestService itemRequestService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testCreateItemRequest() {
        User user = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        itemRequest.setCreated(LocalDateTime.now());
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);

        ItemRequest createdItemRequest = itemRequestService.createItemRequest(itemRequest);

        Assertions.assertThat(createdItemRequest).isEqualTo(itemRequest);

    }

    @Test
    public void testCreateItemRequestWithNullDate() {

        LocalDateTime beforeTest = LocalDateTime.now();

        User user = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        itemRequest.setCreated(null);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);

        ItemRequest createdItemRequest = itemRequestService.createItemRequest(itemRequest);

        Assertions.assertThat(createdItemRequest).isEqualTo(itemRequest);
        Assertions.assertThat(createdItemRequest.getCreated()).isNotNull();
        Assertions.assertThat(createdItemRequest.getCreated()).isAfterOrEqualTo(beforeTest);
    }

    @Test
    public void testFindAllItemRequest() {
        List<ItemRequest> itemRequests = new ArrayList<>();

        User user = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);

        itemRequests.add(itemRequest);

        when(itemRequestRepository.findAllByRequestorId(user.getId())).thenReturn(itemRequests);

        itemRequests = itemRequestService.findAllItemRequest(user.getId());

        Assertions.assertThat(itemRequests.size()).isEqualTo(1);
    }

    @Test
    public void testFindByIdSilent() {
        User user = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);

        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));

        ItemRequest itemRequestGet = itemRequestService.findByIdSilent(itemRequest.getId());

        Assertions.assertThat(itemRequestGet).isEqualTo(itemRequest); // просто дёрнуть мок
    }

    @Test
    public void testFindAllItemRequestPagination() {
        int from = 0;
        int size = 1;
        List<ItemRequest> itemRequests = new ArrayList<>();
        User user = UserTestData.getUserOne();
        ItemRequest itemRequestOne = ItemRequestTestData.getItemRequest(user);
        ItemRequest itemRequestTwo = ItemRequestTestData.getItemRequest(user);

        itemRequests.add(itemRequestOne);
        itemRequests.add(itemRequestTwo);

        Pageable pageable = PageRequest.of(from, size);
        Page<ItemRequest> itemRequestPage = new PageImpl<>(itemRequests, pageable, 2);

        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(user.getId(), pageable)).thenReturn(itemRequestPage);

        List<ItemRequest> itemRequestList = itemRequestService.findAllItemRequests(user.getId(), from, size);

        Assertions.assertThat(itemRequestList).isNotNull();
        Assertions.assertThat(itemRequestList).hasSize(2);


    }

    @Test
    public void testFindAllItemRequestWrongPagination() {
        int from = -1;
        int size = 0;
        Long userId = 1L;
        assertThrows(IllegalArgumentException.class, () -> PageRequest.of(from, size));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> itemRequestService.findAllItemRequests(userId, from, size));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Неверный номер страницы")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));


    }

    @Test
    public void testFindByIdNotFound() {
        User user = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> itemRequestService.findById(itemRequest.getId(), user.getId()));


        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Такой запрос не найден")
                .asInstanceOf(InstanceOfAssertFactories.type(NotFoundException.class));


    }

    @Test
    public void testFindById() {
        User user = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);

        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));

        ItemRequest itemRequest1 = itemRequestService.findById(itemRequest.getId(), user.getId());

        Assertions.assertThat(itemRequest1).isEqualTo(itemRequest);

    }

}
