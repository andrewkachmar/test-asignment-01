package com.teamvoy.teamvoytestasignment.utils;

import com.teamvoy.teamvoytestasignment.domain.GoodEntity;
import com.teamvoy.teamvoytestasignment.domain.LineItemEntity;
import com.teamvoy.teamvoytestasignment.domain.OrderEntity;
import com.teamvoy.teamvoytestasignment.dto.CreateLineItemDto;
import com.teamvoy.teamvoytestasignment.dto.PlaceOrderDto;
import com.teamvoy.teamvoytestasignment.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataGenerateUtils {
    public static List<OrderEntity> generateRandomOrders(int count) {
        return IntStream.range(0, count)
                .mapToObj(DataGenerateUtils::getRandomOrder)
                .collect(Collectors.toList());
    }

    public static List<GoodEntity> generateRandomGoods(int count) {
        return IntStream.range(0, count)
                .mapToObj(DataGenerateUtils::getRandomGood)
                .collect(Collectors.toList());
    }

    private static GoodEntity getRandomGood(int i) {
        return GoodEntity.builder()
                .name("Good-" + i)
                .price(Math.random() * 100)
                .quantity((int) (Math.random() * 100))
                .created(LocalDateTime.now())
                .build();
    }

    private static OrderEntity getRandomOrder(int i) {
        return OrderEntity.builder()
                .id((long) i)
                .orderStatus(OrderStatus.NEW)
                .lineItems(generateRandomLines(new Random().nextInt(10)))
                .build();
    }

    public static List<CreateLineItemDto> generateRandomCreateLines(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> CreateLineItemDto.builder()
                        .goodId((long) i)
                        .lineQuantity(i + 1)
                        .build())
                .collect(Collectors.toList());
    }

    public static List<LineItemEntity> generateRandomLines(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> LineItemEntity.builder()
                        .id((long) i)
                        .good(getRandomGood(i))
                        .linePrice(Math.random() * 100)
                        .lineQuantity((int) (Math.random() * 100))
                        .build())
                .collect(Collectors.toList());
    }

    public static PlaceOrderDto createSamplePlaceOrderDto(int count) {
        return PlaceOrderDto.builder()
                .lineItems(generateRandomCreateLines(count))
                .build();
    }

    public static OrderEntity createSampleOrderEntity() {
        return createSampleOrderEntity(new Random().nextInt(10));
    }

    public static OrderEntity createSampleOrderEntity(int count) {
        return OrderEntity.builder()
                .orderStatus(OrderStatus.NEW)
                .created(LocalDateTime.now())
                .lineItems(generateRandomLines(count))
                .build();
    }
}
