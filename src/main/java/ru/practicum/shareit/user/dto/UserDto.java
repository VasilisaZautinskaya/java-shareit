package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    protected Long id = null;
    @NotNull
    protected String name = null;
    @Email
    @NotNull
    protected String email = null;
}

