package com.teamvoy.teamvoytestasignment.exceptions.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ValidationErrorResponseDto {
    private String field;
    private List<String> errors;
}
