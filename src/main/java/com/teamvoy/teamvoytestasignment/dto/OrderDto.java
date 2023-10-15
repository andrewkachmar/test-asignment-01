package com.teamvoy.teamvoytestasignment.dto;
import com.teamvoy.teamvoytestasignment.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@ToString
@AllArgsConstructor
public final class OrderDto {
    private final Long id;
    private final LocalDateTime created;
    private final OrderStatus orderStatus;
    private final List<LineItemDto> lineItems;
}
