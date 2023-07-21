package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.testData.ItemRequestTestData;
import ru.practicum.shareit.testData.ItemTestData;
import ru.practicum.shareit.testData.UserTestData;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingService bookingService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateItem() {
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);

        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item createdItem = itemService.createItem(item, user.getId(), itemRequest.getId());

        Assertions.assertThat(createdItem).isEqualTo(item);
        Assertions.assertThat(createdItem).isNotNull();

        verify(itemRepository).save(any(Item.class));

    }

    @Test
    public void testUpdateItemName() {
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);

        String name = "ItemNameOne";

        item.setName(name);
        when(itemRepository.save(any(Item.class))).thenReturn(item);


        Assertions.assertThat(item.getName()).isEqualTo(name);

    }

    @Test
    public void testUpdateItemDescription() {
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);

        String description = "ItemNameOne";

        item.setDescription(description);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Assertions.assertThat(item.getDescription()).isEqualTo(description);

    }

    @Test
    public void testUpdateItemOwner() {
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);

        User userNewOwner = UserTestData.getUser();

        item.setOwner(userNewOwner);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Assertions.assertThat(item.getOwner()).isEqualTo(userNewOwner);

    }

    @Test
    public void testUpdateItemAvailable() {
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);

        Boolean available = false;

        item.setAvailable(available);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Assertions.assertThat(item.getAvailable()).isEqualTo(available);
    }

    @Test
    public void testValidateThatUserIsOwner() {
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item oldItem = ItemTestData.getItemOne(itemRequest, owner);

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> itemService.update(
                oldItem.getId(),
                oldItem,
                user.getId()));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Нельзя обновить вещь, принадлежащую другому пользователю")
                .asInstanceOf(InstanceOfAssertFactories.type(ForbiddenException.class));
    }

    @Test
    public void testDelete() {
        Long itemId = 1L;
        itemService.deleteById(itemId);
        verify(itemRepository).deleteById(itemId);
    }

    @Test
    public void testGetAll() {
        List<Item> items = new ArrayList<>();
        User user = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, user);
        Item itemTwo = ItemTestData.getItemTwo(itemRequest, user);

        items.add(item);
        items.add(itemTwo);

        when(itemRepository.findAll(user.getId())).thenReturn(items);

        Assertions.assertThat(items.size()).isEqualTo(2);
    }
}
