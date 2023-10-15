package com.teamvoy.teamvoytestasignment.integration;

import com.teamvoy.teamvoytestasignment.domain.GoodEntity;
import com.teamvoy.teamvoytestasignment.domain.OrderEntity;
import com.teamvoy.teamvoytestasignment.dto.OrderDto;
import com.teamvoy.teamvoytestasignment.dto.CreateLineItemDto;
import com.teamvoy.teamvoytestasignment.dto.PlaceOrderDto;
import com.teamvoy.teamvoytestasignment.enums.OrderStatus;
import com.teamvoy.teamvoytestasignment.exceptions.models.ValidationErrorResponseDto;
import com.teamvoy.teamvoytestasignment.utils.DataGenerateUtils;
import com.teamvoy.teamvoytestasignment.repos.TestGoodRepository;
import com.teamvoy.teamvoytestasignment.repos.TestOrderRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class OrderControllerTest {
    @Autowired
    private TestGoodRepository goodRepository;
    @Autowired
    private TestOrderRepository orderRepository;
    @Autowired
    private TestRestTemplate restTemplate;
    @Value("${unpaid.order.expiry}")
    private Integer unpaidOrderExpiry;

    @BeforeAll
    public void before() {
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @Test
    public void testPlaceOrder_Positive() {
        List<GoodEntity> goods = goodRepository.saveAll(List.of(
                GoodEntity.builder().name("Good 1").price(10.0).quantity(20).created(LocalDateTime.now()).build(),
                GoodEntity.builder().name("Good 2").price(100.0).quantity(20).created(LocalDateTime.now()).build(),
                GoodEntity.builder().name("Good 3").price(340.0).quantity(20).created(LocalDateTime.now()).build()
        ));
        List<CreateLineItemDto> createLineItemDtos = goods.stream()
                .map(good -> new CreateLineItemDto(good.getId(), 10))
                .collect(Collectors.toList());

        ResponseEntity<OrderDto> response = restTemplate.postForEntity("/orders", new PlaceOrderDto(createLineItemDtos), OrderDto.class);
        OrderDto createdOrder = response.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertNotNull(createdOrder),
                () -> assertNotNull(createdOrder.getId()),
                () -> assertEquals(OrderStatus.NEW, createdOrder.getOrderStatus()),
                () -> assertEquals(createLineItemDtos.size(), createdOrder.getLineItems().size()),
                () -> assertTrue(orderRepository.existsById(createdOrder.getId()))
        );
    }

    @Test
    public void testPlaceOrder_UnpaidRemoval() throws InterruptedException {
        List<GoodEntity> goods = goodRepository.saveAll(List.of(
                GoodEntity.builder().name("Good 1").price(10.0).quantity(20).created(LocalDateTime.now()).build(),
                GoodEntity.builder().name("Good 2").price(100.0).quantity(20).created(LocalDateTime.now()).build(),
                GoodEntity.builder().name("Good 3").price(340.0).quantity(20).created(LocalDateTime.now()).build()
        ));
        List<CreateLineItemDto> createLineItemDtos = goods.stream()
                .map(good -> new CreateLineItemDto(good.getId(), 10))
                .collect(Collectors.toList());

        ResponseEntity<OrderDto> response = restTemplate.postForEntity("/orders", new PlaceOrderDto(createLineItemDtos), OrderDto.class);
        OrderDto createdOrder = response.getBody();

        Thread.sleep(unpaidOrderExpiry * 1000);
        assertAll(
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertNotNull(createdOrder),
                () -> assertNotNull(createdOrder.getId()),
                () -> assertEquals(OrderStatus.NEW, createdOrder.getOrderStatus()),
                () -> assertEquals(createLineItemDtos.size(), createdOrder.getLineItems().size()),
                () -> assertFalse(orderRepository.existsById(createdOrder.getId()))
        );
    }

    @Test
    public void testPlaceOrder_DuplicateGoods() {
        goodRepository.saveAll(List.of(
                new GoodEntity(1L, "Good1", 10.0, 20, LocalDateTime.now()),
                new GoodEntity(2L, "Good2", 10.0, 20, LocalDateTime.now())
        ));
        PlaceOrderDto placeOrderDto = new PlaceOrderDto(List.of(
                new CreateLineItemDto(1L, 10),
                new CreateLineItemDto(1L, 10)
        ));

        ResponseEntity<OrderDto> response = restTemplate.postForEntity("/orders", placeOrderDto, OrderDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testPlaceOrder_GoodsNotFound() {
        PlaceOrderDto placeOrderDto = new PlaceOrderDto(DataGenerateUtils.generateRandomCreateLines(10));

        ResponseEntity<OrderDto> response = restTemplate.postForEntity("/orders", placeOrderDto, OrderDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testPlaceOrder_InvalidQuantity() {
        List<CreateLineItemDto> createLineItemDtos = List.of(new CreateLineItemDto(1L, -100));

        ResponseEntity<ValidationErrorResponseDto[]> response = restTemplate.postForEntity("/orders", new PlaceOrderDto(createLineItemDtos), ValidationErrorResponseDto[].class);
        ValidationErrorResponseDto[] responseBody = response.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertEquals(1, responseBody.length)
        );
    }

    @Test
    public void testPayForOrder_Positive() {
        OrderEntity orderEntity = orderRepository.save(OrderEntity.builder()
                .created(LocalDateTime.now())
                .lineItems(new ArrayList<>())
                .orderStatus(OrderStatus.NEW)
                .build());

        ResponseEntity<Void> response = restTemplate.exchange("/orders/" + orderEntity.getId(), HttpMethod.PATCH, null, Void.class);

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()),
                () -> assertEquals(0, orderRepository.findAllByOrderStatus(OrderStatus.NEW).size()),
                () -> assertEquals(1, orderRepository.findAllByOrderStatus(OrderStatus.PAID).size())
        );
    }

    @Test
    public void testPayForOrder_AlreadyPaid() {
        long orderId = 1L;
        orderRepository.save(new OrderEntity(orderId, LocalDateTime.now(), OrderStatus.PAID, new ArrayList<>()));

        ResponseEntity<Void> response = restTemplate.exchange("/orders/" + orderId, HttpMethod.PATCH, null, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testPayForOrder_NegativeNonExistentOrder() {
        long orderId = 999L;

        ResponseEntity<Void> response = restTemplate.exchange("/orders/" + orderId, HttpMethod.PATCH, null, Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
