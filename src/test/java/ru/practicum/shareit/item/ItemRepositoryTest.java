package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.DbItemRepository;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.testData.ItemRequestTestData;
import ru.practicum.shareit.testData.ItemTestData;
import ru.practicum.shareit.testData.UserTestData;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@DataJpaTest
public class ItemRepositoryTest {

    @Mock
    private JpaItemRepository jpaItemRepository;

    @InjectMocks
    private DbItemRepository dbItemRepository;

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

        when(jpaItemRepository.save(item)).thenReturn(item);

        Item savedItem = dbItemRepository.save(item);

        Assertions.assertThat(savedItem.getId()).isEqualTo(item.getId());
        Assertions.assertThat(savedItem.getDescription()).isEqualTo(item.getDescription());
        Assertions.assertThat(savedItem.getAvailable()).isEqualTo(item.getAvailable());
        Assertions.assertThat(savedItem.getRequest()).isEqualTo(item.getRequest());
    }

    @Test
    public void testDeleteById() {
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        when(jpaItemRepository.save(item)).thenReturn(item);
        dbItemRepository.save(item);
        dbItemRepository.deleteById(item.getId());
        Assertions.assertThat(dbItemRepository.findById(item.getId())).isNull();


    }

    @Test
    public void testGetAll() {
        List<Item> itemList = new ArrayList<>();
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Item item1 = ItemTestData.getItemTwo(itemRequest, owner);
        itemList.add(item1);
        itemList.add(item);
        dbItemRepository.save(item);
        dbItemRepository.save(item1);
        when(jpaItemRepository.findAllByOwnerIdOrderById(owner.getId())).thenReturn(itemList);

        List<Item> getItems = dbItemRepository.findAll(owner.getId());

        Assertions.assertThat(getItems.size()).isEqualTo(2);
    }

    @Test
    public void testGetItemForRequest() {
        List<Item> itemList = new ArrayList<>();
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Item item1 = ItemTestData.getItemTwo(itemRequest, owner);
        itemList.add(item1);
        itemList.add(item);
        dbItemRepository.save(item);
        dbItemRepository.save(item1);
        when(jpaItemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(itemList);

        List<Item> getItems = dbItemRepository.findAllByRequestId(itemRequest.getId());

        Assertions.assertThat(getItems.size()).isEqualTo(2);
    }

    @Test
    public void getSearch() {
        List<Item> itemList = new ArrayList<>();
        String text = "Item";
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Item item1 = ItemTestData.getItemTwo(itemRequest, owner);
        itemList.add(item1);
        itemList.add(item);
        dbItemRepository.save(item);
        dbItemRepository.save(item1);
        when(jpaItemRepository.getSearch(text)).thenReturn(itemList);

        List<Item> getItems = dbItemRepository.getSearch(text);

        Assertions.assertThat(getItems.size()).isEqualTo(2);
    }
}
