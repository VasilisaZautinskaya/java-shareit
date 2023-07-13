package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Data
public class BookingResponseDto {
    private Long id;

    @FutureOrPresent
    @NotNull
    private LocalDateTime start;

    @NotNull
    @Future
    private LocalDateTime end;
    private BookingStatus status;
    private UserDto booker;
    private ItemDto item;


}
