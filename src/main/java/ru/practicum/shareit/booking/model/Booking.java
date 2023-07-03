package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Date;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start_date")
    private Date start;

    @Column(name = "end_time")
    private Date end;

    @Column(name = "item_id")
    @OneToOne(mappedBy = "id")
    private Item item;

    @Column(name = "booker_id")
    @OneToOne(mappedBy = "id", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User booker;


}
