package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BookingRequestDto implements Comparable<BookingRequestDto> {
    protected Long id;

    @NotNull
    @FutureOrPresent
    protected LocalDateTime start;

    @NotNull
    @Future
    protected LocalDateTime end;
    @NotNull
    protected Long itemId;

    protected Long bookerId;

    protected BookingStatus status;

    @Override
    public int compareTo(BookingRequestDto o) {
        return this.getStart().compareTo(o.getStart());
    }
}
