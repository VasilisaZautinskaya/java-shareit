package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemWithBookingDto {

    private Long id;

    private Long ownerId;

    private String name;

    private String description;


    private Boolean available;

    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentDto> comments;
}

