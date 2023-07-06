package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

@Builder
@Data
public class BookingDto {
    private Long id;
    private Date start;
    private Item item;
    private User booker;


}
