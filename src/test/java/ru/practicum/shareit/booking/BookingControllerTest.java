package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.type.TypeReference;
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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.testData.BookingTestData;
import ru.practicum.shareit.testData.ItemRequestTestData;
import ru.practicum.shareit.testData.ItemTestData;
import ru.practicum.shareit.testData.UserTestData;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SneakyThrows
    public void testUpdateBooking() {
        User requester = UserTestData.getUserOne();
        User owner = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        boolean approved = true;

        when(bookingService.approve(booking.getId(), owner.getId(), approved)).thenReturn(booking);
        LocalDateTime startDate = LocalDateTime.now().minusHours(6);
        booking.setStart(startDate);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.patch("/bookings/{bookingId}", booking.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("approved", String.valueOf(approved))
                                .header(X_SHARER_USER_ID, owner.getId())
                                .content(objectMapper.writeValueAsString(booking))
                )
                .andDo(print())
                .andReturn();
        String resultBookingRequest = result.getResponse().getContentAsString();
        BookingResponseDto bookingResponseDto = objectMapper.readValue(resultBookingRequest, BookingResponseDto.class);

        Assertions.assertThat(bookingResponseDto.getId()).isEqualTo(booking.getId());


    }

    @Test
    @SneakyThrows
    public void testGetBooking() {
        User requester = UserTestData.getUserOne();
        User owner = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);

        when(bookingService.getById(booking.getId(), owner.getId())).thenReturn(booking);


        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/bookings/{bookingId}", booking.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(X_SHARER_USER_ID, owner.getId())
                                .content(objectMapper.writeValueAsString(booking))
                )
                .andDo(print())
                .andReturn();
        String resultBookingRequest = result.getResponse().getContentAsString();
        BookingResponseDto bookingResponseDto = objectMapper.readValue(resultBookingRequest, BookingResponseDto.class);

        Assertions.assertThat(bookingResponseDto.getId()).isEqualTo(booking.getId());

    }

    @Test
    @SneakyThrows
    public void testGetAllBooking() {
        User requester = UserTestData.getUserOne();
        User owner = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        List<Booking> bookings = BookingTestData.createBookingList(owner, item);
        String state = "ALL";
        int from = 0;
        int size = 10;
        List<BookingResponseDto> bookingResponseDtoList = BookingMapper.toBookingResponseListDto(bookings);

        when(bookingService.findAllBookings(owner.getId(), state, from, size)).thenReturn(bookingResponseDtoList);


        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(X_SHARER_USER_ID, owner.getId())
                                .param("state", state)
                                .param("from", String.valueOf(from))
                                .param("size", String.valueOf(size))
                                .content(objectMapper.writeValueAsString(bookingResponseDtoList))
                )
                .andDo(print())
                .andReturn();

        String resultBookingStr = result.getResponse().getContentAsString();
        List<BookingResponseDto> bookingResponseDtos = objectMapper.readValue(resultBookingStr, new TypeReference<List<BookingResponseDto>>() {
        });

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(bookingResponseDtos.size()).isEqualTo(5);


    }

}
