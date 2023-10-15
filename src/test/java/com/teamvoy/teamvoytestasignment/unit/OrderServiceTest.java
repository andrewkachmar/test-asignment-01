package com.teamvoy.teamvoytestasignment.unit;

import com.teamvoy.teamvoytestasignment.domain.GoodEntity;
import com.teamvoy.teamvoytestasignment.domain.OrderEntity;
import com.teamvoy.teamvoytestasignment.dto.OrderDto;
import com.teamvoy.teamvoytestasignment.dto.PlaceOrderDto;
import com.teamvoy.teamvoytestasignment.enums.OrderStatus;
import com.teamvoy.teamvoytestasignment.exceptions.models.ResourceNotFoundException;
import com.teamvoy.teamvoytestasignment.repositories.OrderRepository;
import com.teamvoy.teamvoytestasignment.services.GoodService;
import com.teamvoy.teamvoytestasignment.services.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.teamvoy.teamvoytestasignment.utils.DataGenerateUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private GoodService goodService;


    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 20, 50})
    public void testPlaceOrder_Success(int count) {
        PlaceOrderDto placeOrderDto = createSamplePlaceOrderDto(count);
        OrderEntity orderEntity = createSampleOrderEntity(count);

        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(goodService.checkAvailability(anyLong(), anyInt())).thenReturn(true);
        when(goodService.findGoodById(anyLong())).thenReturn(new GoodEntity());

        OrderDto result = orderService.placeOrder(placeOrderDto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(OrderStatus.NEW, result.getOrderStatus()),
                () -> assertEquals(placeOrderDto.getLineItems().size(), result.getLineItems().size())
        );
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(goodService, times(count)).checkAvailability(anyLong(), anyInt());
        verify(goodService, times(count)).findGoodById(anyLong());
    }

    @Test
    public void testPlaceOrder_InsufficientQuantity() {
        PlaceOrderDto placeOrderDto = createSamplePlaceOrderDto(1);

        when(goodService.checkAvailability(anyLong(), anyInt())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> orderService.placeOrder(placeOrderDto));

        verify(goodService, times(1)).checkAvailability(anyLong(), anyInt());
    }

    @Test
    public void testPayForOrder_Success() {
        Long orderId = 1L;
        OrderEntity orderEntity = createSampleOrderEntity();
        OrderEntity paidOrder = createSampleOrderEntity().toBuilder().orderStatus(OrderStatus.PAID).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        doNothing().when(goodService).changeAmounts(anyList());
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(paidOrder);

        orderService.payForOrder(orderId);

        assertEquals(OrderStatus.PAID, paidOrder.getOrderStatus());

        verify(orderRepository, times(1)).findById(orderId);
        verify(goodService, times(1)).changeAmounts(anyList());
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
    }

    @Test
    public void testPayForOrder_OrderNotFound() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.payForOrder(orderId));

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    public void testPayForOrder_AlreadyPaid() {
        Long orderId = 1L;
        OrderEntity orderEntity = createSampleOrderEntity(10).toBuilder()
                .orderStatus(OrderStatus.PAID)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));

        assertThrows(IllegalArgumentException.class, () -> orderService.payForOrder(orderId));

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    public void testDeleteUnpaidOrder_Success() {
        Long orderId = 1L;
        OrderEntity orderEntity = OrderEntity.builder().orderStatus(OrderStatus.NEW).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));

        orderService.deleteUnpaidOrder(orderId);

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    public void testDeleteUnpaidOrder_PaidOrder() {
        Long orderId = 1L;
        OrderEntity orderEntity = OrderEntity.builder().orderStatus(OrderStatus.PAID).build();

        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(orderEntity));

        orderService.deleteUnpaidOrder(orderId);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).deleteById(orderId);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 10, 20, 50})
    public void testFindAll(int numberOfOrders) {
        List<OrderEntity> orders = generateRandomOrders(numberOfOrders);
        when(orderRepository.findAll()).thenReturn(orders);

        List<OrderDto> result = orderService.findAll();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(orders.size(), result.size())
        );
        verify(orderRepository, times(1)).findAll();
    }
}
