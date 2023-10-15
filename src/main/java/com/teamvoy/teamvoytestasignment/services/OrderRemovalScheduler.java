package com.teamvoy.teamvoytestasignment.services;

import com.teamvoy.teamvoytestasignment.dto.OrderDto;

public interface OrderRemovalScheduler {
    void scheduleOrderPaymentCheck(OrderDto orderDto, Integer duration);
}
