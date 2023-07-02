package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    private Long id;
    private String description;
    private User requestor;
    private Date created;

}
