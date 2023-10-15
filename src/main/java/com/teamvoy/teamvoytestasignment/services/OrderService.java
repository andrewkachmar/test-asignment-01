package com.teamvoy.teamvoytestasignment.services;

import com.teamvoy.teamvoytestasignment.dto.OrderDto;
import com.teamvoy.teamvoytestasignment.dto.PlaceOrderDto;

import java.util.List;

public interface OrderService {
    OrderDto placeOrder(PlaceOrderDto placeOrderDto);
    List<OrderDto> findAll();
    void payForOrder(Long orderId);

    void deleteUnpaidOrder(Long orderId);
}
