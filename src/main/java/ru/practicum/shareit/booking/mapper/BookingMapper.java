package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static Booking toBooking(BookingRequestDto booking, Item item, User user) {
        return Booking.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingRequestDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingRequestDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }


    public static List<BookingResponseDto> toBookingResponseListDto(List<Booking> allByUser) {
        return allByUser.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    public static BookingForItemDto mapToBookingForItemDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingForItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

}
