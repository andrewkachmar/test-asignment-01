package com.teamvoy.teamvoytestasignment.exceptions.models;

import lombok.*;

@AllArgsConstructor
@ToString
@Builder
@Getter
public final class ErrorResponseDto {
    private final Integer code;
    private final String message;
    private final String path;
}
