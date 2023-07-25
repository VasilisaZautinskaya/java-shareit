package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.testData.BookingTestData;
import ru.practicum.shareit.testData.ItemRequestTestData;
import ru.practicum.shareit.testData.ItemTestData;
import ru.practicum.shareit.testData.UserTestData;
import ru.practicum.shareit.user.model.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    ItemService itemService;
    @MockBean
    BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SneakyThrows
    public void testCreateItem() {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(itemService.createItem(any(Item.class), eq(owner.getId()), eq(itemRequest.getId()))).thenReturn(item);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", owner.getId())
                                .content(objectMapper.writeValueAsString(itemDto))
                )
                .andDo(print())
                .andReturn();

        String resultItemStr = result.getResponse().getContentAsString();
        ItemDto resultItemDto = objectMapper.readValue(resultItemStr, ItemDto.class);

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        Assertions.assertThat(resultItemDto.getId()).isEqualTo(item.getId());
        Assertions.assertThat(resultItemDto.getName()).isEqualTo(item.getName());
        Assertions.assertThat(resultItemDto.getAvailable()).isEqualTo(item.getAvailable());
        Assertions.assertThat(resultItemDto.getOwner()).isEqualTo(item.getOwner());
        Assertions.assertThat(resultItemDto.getDescription()).isEqualTo(item.getDescription());
        Assertions.assertThat(resultItemDto.getRequestId()).isEqualTo(item.getRequest().getId());
        Assertions.assertThat(resultItemDto).isNotNull();
    }

    @Test
    @SneakyThrows
    public void testItemWithBookingDto() {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(user, item);
        Booking booking2 = BookingTestData.getBookingTwo(user, item);
        when(bookingService.getLastBookingForItem(eq(item.getId()))).thenReturn(booking);
        when(bookingService.getNextBookingForItem(eq(item.getId()))).thenReturn(booking2);
        when(itemService.findById(item.getId())).thenReturn(item);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/items/{itemId}", item.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", owner.getId())
                )
                .andDo(print())
                .andReturn();

        String resultItemStr = result.getResponse().getContentAsString();
        ItemWithBookingDto resultItemDto = objectMapper.readValue(resultItemStr, ItemWithBookingDto.class);

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(resultItemDto.getId()).isEqualTo(item.getId());
        Assertions.assertThat(resultItemDto.getName()).isEqualTo(item.getName());
        Assertions.assertThat(resultItemDto.getAvailable()).isEqualTo(item.getAvailable());
        Assertions.assertThat(resultItemDto.getDescription()).isEqualTo(item.getDescription());
        Assertions.assertThat(resultItemDto.getNextBooking()).isNotNull();
        Assertions.assertThat(resultItemDto.getNextBooking().getId()).isEqualTo(booking2.getId());
        Assertions.assertThat(resultItemDto.getLastBooking()).isNotNull();
        Assertions.assertThat(resultItemDto.getLastBooking().getId()).isEqualTo(booking.getId());
        Assertions.assertThat(resultItemDto).isNotNull();
    }


    @Test
    @SneakyThrows
    public void testUpdateItem() {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);

        when(itemService.update(eq(item.getId()), any(Item.class), eq(owner.getId()))).thenReturn(item);


        String name = "ItemNameOne";
        item.setName(name);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.patch("/items/{itemId}", item.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", owner.getId())
                                .content(objectMapper.writeValueAsString(itemDto))
                )
                .andDo(print())
                .andReturn();


        String resultItemStr = result.getResponse().getContentAsString();
        ItemDto resultItemDto = objectMapper.readValue(resultItemStr, ItemDto.class);

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        Assertions.assertThat(resultItemDto.getId()).isEqualTo(item.getId());
        Assertions.assertThat(resultItemDto.getName()).isEqualTo(item.getName());
        Assertions.assertThat(resultItemDto.getAvailable()).isEqualTo(item.getAvailable());
        Assertions.assertThat(resultItemDto.getOwner()).isEqualTo(item.getOwner());
        Assertions.assertThat(resultItemDto.getDescription()).isEqualTo(item.getDescription());
        Assertions.assertThat(resultItemDto.getRequestId()).isEqualTo(item.getRequest().getId());
        Assertions.assertThat(resultItemDto).isNotNull();
    }

}
