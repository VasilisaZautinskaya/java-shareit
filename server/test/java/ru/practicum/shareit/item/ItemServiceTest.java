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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.testData.CommentTestData;
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
        Item item = ItemTestData.getItemOne(itemRequest, null);


        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item createdItem = itemService.createItem(item, owner.getId(), itemRequest.getId());

        Assertions.assertThat(createdItem).isEqualTo(item);
        Assertions.assertThat(createdItem).isNotNull();

        verify(itemRepository).save(any(Item.class));

    }

    @Test
    public void testCreateItemWithoutItemRequest() {
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = null;
        Item item = ItemTestData.getItemOne(itemRequest, owner);


        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item createdItem = itemService.createItem(item, owner.getId(), null);

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
        when(itemRepository.findById(item.getId())).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item updateItem = itemService.update(item.getId(), item, owner.getId());

        Assertions.assertThat(updateItem.getName()).isEqualTo(name);
        verify(itemRepository).findById(item.getId());
        verify(itemRepository).save(item);

    }

    @Test
    public void testUpdateItemDescription() {
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);

        String description = "ItemNameOne";

        item.setDescription(description);

        when(itemRepository.findById(item.getId())).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);

        Item updateItem = itemService.update(item.getId(), item, owner.getId());

        Assertions.assertThat(updateItem.getDescription()).isEqualTo(description);

    }

    @Test
    public void testUpdateItemOwner() {
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);

        User userNewOwner = UserTestData.getUser();

        item.setOwner(userNewOwner);

        when(itemRepository.findById(item.getId())).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item updateItem = itemService.update(item.getId(), item, userNewOwner.getId());

        Assertions.assertThat(updateItem.getOwner()).isEqualTo(userNewOwner);

    }

    @Test
    public void testUpdateItemAvailable() {
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);

        Boolean available = false;

        item.setAvailable(available);

        when(itemRepository.findById(item.getId())).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item updateItem = itemService.update(item.getId(), item, owner.getId());
        Assertions.assertThat(updateItem.getAvailable()).isEqualTo(available);
    }

    @Test
    public void testValidateThatUserIsOwner() {

        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item oldItem = ItemTestData.getItemOne(itemRequest, owner);


        when(itemRepository.findById(oldItem.getId())).thenReturn(oldItem);
        when(itemRepository.save(any(Item.class))).thenReturn(oldItem);

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

        User user = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);

        Item item = ItemTestData.getItemOne(itemRequest, user);
        Item itemTwo = ItemTestData.getItemTwo(itemRequest, user);

        items.add(item);
        items.add(itemTwo);

        when(itemRepository.findAll(user.getId())).thenReturn(items);

        items = itemService.findAll(user.getId());

        Assertions.assertThat(items.size()).isEqualTo(2);
    }

    @Test
    public void testFindById() {

        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);

        when(itemRepository.findById(item.getId())).thenReturn(item);

        Item getItem = itemService.findById(owner.getId());

        Assertions.assertThat(getItem).isEqualTo(item);
    }

    @Test
    public void testFindNullItem() {
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> itemService.findById(item.getId()));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Вещь не найдена")
                .asInstanceOf(InstanceOfAssertFactories.type(NotFoundException.class));
    }

    @Test
    public void testFindItemsByText() {
        Item item = ItemTestData.getItemOne(null, UserTestData.getUserOne());

        String text = "find me!";
        when(itemRepository.getSearch(text)).thenReturn(List.of(item));

        List<Item> itemsFromSearch = itemService.findItemsByText(text);

        Assertions.assertThat(itemsFromSearch).hasSize(1);
        Assertions.assertThat(itemsFromSearch.get(0)).isNotNull();
        Assertions.assertThat(itemsFromSearch.get(0).getId()).isEqualTo(item.getId());

        verify(itemRepository).getSearch(text);
    }

    @Test
    public void testFindItemsByEmptyText() {
        List<Item> itemsByEmptyText = itemService.findItemsByText("");
        Assertions.assertThat(itemsByEmptyText).isNotNull();
        Assertions.assertThat(itemsByEmptyText.size()).isEqualTo(0);
    }

    @Test
    public void testPostCommentNotFoundItem() {

        User owner = UserTestData.getUserOne();
        User author = UserTestData.getUserTwo();
        Item item = ItemTestData.getItemOne(null, owner);

        when(userService.findById(author.getId())).thenReturn(author);
        when(itemRepository.findById(item.getId())).thenReturn(null);

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> itemService.postComment(
                item.getId(),
                author.getId(),
                CommentTestData.getCommentOne(owner, item)
        ));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Вещь не найдена")
                .asInstanceOf(InstanceOfAssertFactories.type(NotFoundException.class));

    }

    @Test
    public void testPostCommentUserHasNotBookedItem() {

        User owner = UserTestData.getUserOne();
        User author = UserTestData.getUserTwo();
        Item item = ItemTestData.getItemOne(null, owner);

        when(userService.findById(author.getId())).thenReturn(author);
        when(itemRepository.findById(item.getId())).thenReturn(item);
        when(bookingService.isUserBookedItem(author, item)).thenReturn(false);

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> itemService.postComment(
                item.getId(),
                author.getId(),
                CommentTestData.getCommentOne(owner, item)
        ));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Вы не можете оставить комментарий, так как не бронировали эту вещь")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));
    }

    @Test
    public void testPostComment() {
        User owner = UserTestData.getUserOne();
        User author = UserTestData.getUserTwo();
        Item item = ItemTestData.getItemOne(null, owner);
        Comment commentOne = CommentTestData.getCommentOne(owner, item);

        when(userService.findById(author.getId())).thenReturn(author);
        when(itemRepository.findById(item.getId())).thenReturn(item);
        when(bookingService.isUserBookedItem(author, item)).thenReturn(true);
        when(commentRepository.save(commentOne)).thenReturn(commentOne);

        Comment savedComment = itemService.postComment(
                item.getId(),
                author.getId(),
                commentOne
        );

        verify(commentRepository).save(commentOne);

        Assertions.assertThat(savedComment.getId()).isEqualTo(commentOne.getId());
        Assertions.assertThat(savedComment.getText()).isEqualTo(commentOne.getText());
        Assertions.assertThat(savedComment.getItem()).isEqualTo(commentOne.getItem());
        Assertions.assertThat(savedComment.getAuthor()).isEqualTo(commentOne.getAuthor());
        Assertions.assertThat(savedComment.getCreated()).isEqualTo(commentOne.getCreated());

    }

    @Test
    public void testFindAllByItemId() {
        List<Comment> comments = new ArrayList<>();

        User owner = UserTestData.getUserOne();
        User author = UserTestData.getUserTwo();
        Item item = ItemTestData.getItemOne(null, owner);
        Comment commentOne = CommentTestData.getCommentOne(owner, item);
        Comment commentTwo = CommentTestData.getCommentTwo(author, item);

        comments.add(commentOne);
        comments.add(commentTwo);

        when(commentRepository.findAllByItemId(item.getId())).thenReturn(comments);

        comments = itemService.findAllByItemId(item.getId());

        Assertions.assertThat(comments.size()).isEqualTo(2);
    }

    @Test
    public void testFindAllItemForRequest() {
        List<Item> items = new ArrayList<>();

        User user = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);

        Item item = ItemTestData.getItemOne(itemRequest, user);
        Item itemTwo = ItemTestData.getItemTwo(itemRequest, user);

        items.add(item);
        items.add(itemTwo);

        when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(items);

        items = itemService.findAllItemForRequest(itemRequest.getId());

        Assertions.assertThat(items.size()).isEqualTo(2);
    }
}
