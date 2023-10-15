package com.teamvoy.teamvoytestasignment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public final class LineItemDto {
    private final Long id;
    private final GoodDto good;
    private final Double linePrice;
    private final Integer lineQuantity;
}
