package pl.ing.trading.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.ing.trading.enums.OrderSide;
import pl.ing.trading.enums.OrderStatus;
import pl.ing.trading.dto.OrderStatusResponse;
import pl.ing.trading.entity.OrderEntity;
import pl.ing.trading.integration.ExchangeClient;
import pl.ing.trading.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStatusPollingServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ExchangeClient exchangeClient;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderStatusPollingService pollingService;

    @Test
    void shouldUpdateOrderAndNotifyWhenFilled() {
        OrderEntity order = createSubmittedOrder();
        when(orderRepository.findByStatus(OrderStatus.SUBMITTED)).thenReturn(List.of(order));
        when(exchangeClient.getOrderStatus(1111111L)).thenReturn(
                new OrderStatusResponse(1111111L, OrderStatus.FILLED, "PLBSK0000017",
                        OrderSide.BUY, "PLN", 10, new BigDecimal("315"), 1762444418L, 1762448027L));

        pollingService.pollPendingOrders();

        verify(orderRepository).save(order);
        verify(notificationService).addEmailNotificationTask(any());
        assertEquals(OrderStatus.FILLED, order.getStatus());
        assertEquals(new BigDecimal("315"), order.getExecutionPrice());
    }

    @Test
    void shouldNotSaveWhenStatusUnchanged() {
        OrderEntity order = createSubmittedOrder();
        when(orderRepository.findByStatus(OrderStatus.SUBMITTED)).thenReturn(List.of(order));
        when(exchangeClient.getOrderStatus(1111111L)).thenReturn(
                new OrderStatusResponse(1111111L, OrderStatus.SUBMITTED, "PLBSK0000017",
                        OrderSide.BUY, "PLN", 10, null, 1762444418L, null));

        pollingService.pollPendingOrders();

        verify(orderRepository, never()).save(any());
        verify(notificationService, never()).addEmailNotificationTask(any());
    }

    @Test
    void shouldHandleExchangeClientError() {
        OrderEntity order = createSubmittedOrder();
        when(orderRepository.findByStatus(OrderStatus.SUBMITTED)).thenReturn(List.of(order));
        when(exchangeClient.getOrderStatus(1111111L)).thenThrow(new RuntimeException("Connection refused"));

        pollingService.pollPendingOrders();

        verify(orderRepository, never()).save(any());
    }

    private OrderEntity createSubmittedOrder() {
        return OrderEntity.builder()
                .orderId(1111111L)
                .isin("PLBSK0000017")
                .side(OrderSide.BUY)
                .tradeCurrency("PLN")
                .quantity(10)
                .status(OrderStatus.SUBMITTED)
                .mic("XWAR")
                .build();
    }
}

