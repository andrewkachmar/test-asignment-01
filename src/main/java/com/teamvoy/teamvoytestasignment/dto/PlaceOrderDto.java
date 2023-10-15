package com.teamvoy.teamvoytestasignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class PlaceOrderDto {
    @NotNull
    @Size(min = 1)
    @Schema(description = "A list of line items for the order", minLength = 1)
    private List<@Valid CreateLineItemDto> lineItems;
}
