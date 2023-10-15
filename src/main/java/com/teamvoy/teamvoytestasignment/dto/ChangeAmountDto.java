package com.teamvoy.teamvoytestasignment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public final class ChangeAmountDto {
    @NotNull
    private Long goodId;
    @NotNull
    @Positive
    @Min(1)
    private Integer amount;
}
