package com.teamvoy.teamvoytestasignment.api;

import com.teamvoy.teamvoytestasignment.dto.GoodDto;
import com.teamvoy.teamvoytestasignment.dto.OrderDto;
import com.teamvoy.teamvoytestasignment.dto.PlaceOrderDto;
import com.teamvoy.teamvoytestasignment.exceptions.models.ErrorResponseDto;
import com.teamvoy.teamvoytestasignment.services.OrderService;
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

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "Orders", description = "Endpoints for managing orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Place order", description = "Creates new order for selected items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = GoodDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PostMapping
    public ResponseEntity<OrderDto> placeOrder(@RequestBody @Valid PlaceOrderDto placeOrderDto) {
        return new ResponseEntity<>(orderService.placeOrder(placeOrderDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Pay for order", description = "Stub method to change order status to PAID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order status successfully changed to paid",
                    content = @Content(schema = @Schema(implementation = GoodDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PatchMapping("/{orderId}")
    public ResponseEntity<Void> payForOrder(@PathVariable @NotNull Long orderId) {
        orderService.payForOrder(orderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
