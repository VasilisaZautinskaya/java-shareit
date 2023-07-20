package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    protected Long id;

    @FutureOrPresent
    @NotNull
    protected LocalDateTime start;

    @NotNull
    @Future
    protected LocalDateTime end;
    protected BookingStatus status;
    protected UserDto booker;
    protected ItemDto item;


}
