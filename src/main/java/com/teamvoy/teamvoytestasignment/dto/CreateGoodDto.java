package com.teamvoy.teamvoytestasignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public final class CreateGoodDto {
    @NotBlank
    @Size(min = 5, max = 30)
    @Schema(description = "The name of the good", example = "iPhone 13 Pro", maxLength = 30)
    private final String name;
    @NotNull
    @Positive
    @Schema(description = "The price of the good", example = "45000")
    private final Double price;
    @NotNull
    @Positive
    @Min(1)
    @Schema(description = "The quantity of the good", minimum = "1")
    private final Integer quantity;
}
