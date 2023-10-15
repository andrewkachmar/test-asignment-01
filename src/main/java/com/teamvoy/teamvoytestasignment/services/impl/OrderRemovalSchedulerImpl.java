package com.teamvoy.teamvoytestasignment.services.impl;

import com.teamvoy.teamvoytestasignment.dto.OrderDto;
import com.teamvoy.teamvoytestasignment.services.OrderRemovalScheduler;
import com.teamvoy.teamvoytestasignment.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OrderRemovalSchedulerImpl implements OrderRemovalScheduler {
    private final OrderService orderService;
    private final ScheduledExecutorService executorService;

    @Override
    public void scheduleOrderPaymentCheck(OrderDto orderDto, Integer duration) {
        LocalDateTime gap = orderDto.getCreated().plusSeconds(duration);
        long initialDelay = Duration.between(LocalDateTime.now(), gap).toMillis();
        if (initialDelay > 0) {
            executorService.schedule(() -> orderService.deleteUnpaidOrder(orderDto.getId()), initialDelay, TimeUnit.MILLISECONDS);
        }
    }
}
