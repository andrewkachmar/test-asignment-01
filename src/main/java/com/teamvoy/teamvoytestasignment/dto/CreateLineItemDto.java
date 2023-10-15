package com.teamvoy.teamvoytestasignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class CreateLineItemDto {
    @NotNull
    @Schema(description = "The ID of the good associated with the line item", example = "1")
    private Long goodId;
    @NotNull
    @Positive
    @Min(1)
    @Schema(description = "The quantity of the line item", minimum = "10")
    private Integer lineQuantity;
}
