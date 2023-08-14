package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    JacksonTester<BookingRequestDto> json;

    @Test
    void testBookingDto() throws Exception {
        LocalDateTime startDateTime = LocalDateTime.parse("2023-08-04T13:26:59");
        LocalDateTime endDateTime = LocalDateTime.parse("2023-08-04T13:26:59").plusDays(1);

        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                1L,
                startDateTime,
                endDateTime,
                1L,
                1L,
                BookingStatus.REJECTED
        );


        JsonContent<BookingRequestDto> result = json.write(bookingRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-08-04T13:26:59");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-08-05T13:26:59");
    }
}
