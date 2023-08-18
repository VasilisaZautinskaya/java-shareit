package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.testData.UserTestData;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingResponceDtoTest {
    @Autowired
    private JacksonTester<BookingResponseDto> json;

    @Test
    void testBookingDto() throws Exception {
        LocalDateTime startDateTime = LocalDateTime.parse("2023-08-04T13:26:59");
        LocalDateTime endDateTime = LocalDateTime.parse("2023-08-04T13:26:59").plusDays(1);
        User user = UserTestData.getUserOne();
        ItemDto itemDto = new ItemDto();
        UserDto userDto = UserMapper.toUserDto(user);
        BookingResponseDto bookingResponseDto = new BookingResponseDto(
                1L,
                startDateTime,
                endDateTime,
                BookingStatus.APPROVED,
                userDto,
                itemDto
        );

        JsonContent<BookingResponseDto> result = json.write(bookingResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-08-04T13:26:59");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-08-05T13:26:59");
    }
}
