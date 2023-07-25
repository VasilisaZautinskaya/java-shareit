package ru.practicum.shareit.booking;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.service.BookingService;

import static org.mockito.Mockito.mock;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {
    private final BookingService bookingService = mock(BookingService.class);



}
