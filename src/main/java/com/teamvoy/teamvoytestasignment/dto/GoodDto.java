package com.teamvoy.teamvoytestasignment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public final class GoodDto {
    private final Long id;
    private final String name;
    private final Double price;
    private final Integer quantity;
    private final LocalDateTime created;
}
