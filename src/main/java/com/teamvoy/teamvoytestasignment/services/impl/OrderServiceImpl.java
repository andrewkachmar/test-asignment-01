package com.teamvoy.teamvoytestasignment.services.impl;

import com.teamvoy.teamvoytestasignment.domain.GoodEntity;
import com.teamvoy.teamvoytestasignment.domain.LineItemEntity;
import com.teamvoy.teamvoytestasignment.domain.OrderEntity;
import com.teamvoy.teamvoytestasignment.dto.ChangeAmountDto;
import com.teamvoy.teamvoytestasignment.dto.OrderDto;
import com.teamvoy.teamvoytestasignment.dto.CreateLineItemDto;
import com.teamvoy.teamvoytestasignment.dto.PlaceOrderDto;
import com.teamvoy.teamvoytestasignment.enums.OrderStatus;
import com.teamvoy.teamvoytestasignment.exceptions.models.ResourceNotFoundException;
import com.teamvoy.teamvoytestasignment.mappers.BusinessMapper;
import com.teamvoy.teamvoytestasignment.repositories.OrderRepository;
import com.teamvoy.teamvoytestasignment.services.GoodService;
import com.teamvoy.teamvoytestasignment.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final GoodService goodService;

    @Override
    public OrderDto placeOrder(PlaceOrderDto placeOrderDto) {
        List<LineItemEntity> lineItems = placeOrderDto.getLineItems().stream()
                .map(this::getLineItem)
                .collect(Collectors.toList());
        OrderEntity orderEntity = OrderEntity.builder()
                .orderStatus(OrderStatus.NEW)
                .created(LocalDateTime.now())
                .lineItems(lineItems)
                .build();
        return BusinessMapper.INSTANCE.orderToDto(orderRepository.save(orderEntity));
    }

    @Override
    public List<OrderDto> findAll() {
        return BusinessMapper.INSTANCE.ordersToDto(orderRepository.findAll());
    }

    @Override
    @Transactional
    public void payForOrder(Long orderId) {
        OrderEntity orderEntity = getOrder(orderId);
        if (orderEntity.getOrderStatus().equals(OrderStatus.PAID)) {
            throw new IllegalArgumentException(String.format("Order: %s already paid", orderId));
        }
        List<ChangeAmountDto> changeAmounts = orderEntity.getLineItems().stream()
                .map(line -> new ChangeAmountDto(line.getGood().getId(), line.getLineQuantity()))
                .collect(Collectors.toUnmodifiableList());
        goodService.changeAmounts(changeAmounts);
        orderRepository.save(orderEntity.toBuilder()
                .orderStatus(OrderStatus.PAID)
                .build());
    }

    private OrderEntity getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Order with id: %s is not found", orderId)));
    }

    @Override
    public void deleteUnpaidOrder(Long orderId) {
        OrderEntity orderEntity = getOrder(orderId);
        if (!orderEntity.getOrderStatus().equals(OrderStatus.PAID)) {
            orderRepository.deleteById(orderId);
        }
    }

    private LineItemEntity getLineItem(CreateLineItemDto createLineDto) {
        if (!goodService.checkAvailability(createLineDto.getGoodId(), createLineDto.getLineQuantity())) {
            throw new IllegalArgumentException(String.format("Requested amount: %s is not available", createLineDto.getLineQuantity()));
        }
        GoodEntity good = goodService.findGoodById(createLineDto.getGoodId());
        return LineItemEntity.builder()
                .good(good)
                .linePrice(good.getPrice())
                .lineQuantity(createLineDto.getLineQuantity())
                .build();
    }
}
