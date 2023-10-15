package com.teamvoy.teamvoytestasignment.api;

import com.teamvoy.teamvoytestasignment.dto.GoodDto;
import com.teamvoy.teamvoytestasignment.dto.CreateGoodDto;
import com.teamvoy.teamvoytestasignment.exceptions.models.ErrorResponseDto;
import com.teamvoy.teamvoytestasignment.services.GoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
@Validated
@Tag(name = "Goods", description = "Endpoints for managing goods")
public class GoodController {
    private final GoodService goodService;

    @Operation(summary = "Create good", description = "Add new good to catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Good created successfully",
                    content = @Content(schema = @Schema(implementation = GoodDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PostMapping
    public ResponseEntity<GoodDto> createGood(@RequestBody @Valid CreateGoodDto createGoodDto) {
        return new ResponseEntity<>(goodService.createGood(createGoodDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update good", description = "Update an existing good in catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Good updated successfully",
                    content = @Content(schema = @Schema(implementation = GoodDto.class))),
            @ApiResponse(responseCode = "404", description = "Good is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PutMapping("/{goodId}")
    public ResponseEntity<GoodDto> updateGood(@PathVariable("goodId") @NotNull Long goodId,
                                              @RequestBody @Valid CreateGoodDto createGoodDto) {
        return new ResponseEntity<>(goodService.updateGood(goodId, createGoodDto), HttpStatus.OK);
    }

    @Operation(summary = "Find all", description = "List all goods in catalog")
    @GetMapping
    public ResponseEntity<List<GoodDto>> findAll() {
        return ResponseEntity.ok(goodService.findAll());
    }
}
