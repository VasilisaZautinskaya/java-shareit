package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Data
public class ItemRequestDto {
    private Long id;
    @NotBlank
    @NotNull
    private String description;
    private Long requestorId;
    private LocalDateTime created;
}

