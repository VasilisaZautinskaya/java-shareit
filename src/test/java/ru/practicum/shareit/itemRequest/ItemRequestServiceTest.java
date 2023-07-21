package ru.practicum.shareit.itemRequest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.testData.ItemRequestTestData;
import ru.practicum.shareit.testData.UserTestData;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.mockito.Mockito.when;

public class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

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
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);

        ItemRequest createdItemRequest = itemRequestService.createItemRequest(itemRequest);

        Assertions.assertThat(createdItemRequest).isEqualTo(itemRequest);

    }

}
