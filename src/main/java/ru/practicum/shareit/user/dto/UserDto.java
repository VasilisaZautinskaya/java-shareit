package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


@Value
@Builder
@AllArgsConstructor
public class UserDto {
    protected Long id;
    @NotNull
    protected String name;
    @Email
    @NotNull
    protected String email;
}

