package com.teamvoy.teamvoytestasignment.aspects;

import com.teamvoy.teamvoytestasignment.dto.OrderDto;
import com.teamvoy.teamvoytestasignment.dto.CreateLineItemDto;
import com.teamvoy.teamvoytestasignment.dto.PlaceOrderDto;
import com.teamvoy.teamvoytestasignment.services.OrderRemovalScheduler;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
public class OrderServiceAspect {
    private final OrderRemovalScheduler orderRemovalScheduler;

    @Value("${unpaid.order.expiry}")
    private Integer unpaidOrderExpiry;

    @Pointcut("execution(* com.teamvoy.teamvoytestasignment.services.impl.OrderServiceImpl.placeOrder(..))")
    public void placeOrderPointcut() {
    }

    @Before("placeOrderPointcut()")
    public void beforeOrderPlace(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        PlaceOrderDto placeOrderDto = (PlaceOrderDto) Arrays.stream(args)
                .filter(o -> o instanceof PlaceOrderDto)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid request params"));
        boolean hasDuplicates = placeOrderDto.getLineItems().stream()
                .collect(Collectors.toMap(CreateLineItemDto::getGoodId, item -> item, (existing, replacement) -> existing))
                .size() < placeOrderDto.getLineItems().size();
        if (hasDuplicates) {
            throw new IllegalArgumentException("Duplicate goods found");
        }
    }

    @AfterReturning(pointcut = "placeOrderPointcut()", returning = "orderDto")
    public void scheduleOrderPaymentCheck(OrderDto orderDto) {
        orderRemovalScheduler.scheduleOrderPaymentCheck(orderDto, unpaidOrderExpiry);
    }
}
