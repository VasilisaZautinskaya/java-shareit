package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    Long id;
    String description;
    User requestor;
    Date created;

}
