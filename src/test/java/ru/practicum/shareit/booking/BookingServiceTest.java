package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.testData.BookingTestData;
import ru.practicum.shareit.testData.ItemRequestTestData;
import ru.practicum.shareit.testData.ItemTestData;
import ru.practicum.shareit.testData.UserTestData;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookingServiceTest {

    @Mock
    private JpaBookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateBooking() {
        User user = UserTestData.getUserOne();
        User userTwo = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, userTwo);
        Booking booking = BookingTestData.getBookingOne(user, item);

        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking createBooking = bookingService.create(booking);

        Assertions.assertThat(booking).isEqualTo(createBooking);
        Assertions.assertThat(booking).isNotNull();


        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    public void testNullUser() {
    }


}
