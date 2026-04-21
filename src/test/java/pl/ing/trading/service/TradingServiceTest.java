package pl.ing.trading.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.ing.trading.config.TradingProperties;
import pl.ing.trading.dto.OrderSubmitResponse;
import pl.ing.trading.dto.TickersResponse;
import pl.ing.trading.dto.BuyOrderRequest;
import pl.ing.trading.dto.BuyOrderResponse;
import pl.ing.trading.dto.OrderDetailsResponse;
import pl.ing.trading.entity.OrderEntity;
import pl.ing.trading.enums.OrderStatus;
import pl.ing.trading.enums.OrderType;
import pl.ing.trading.exceptions.OrderDetailsNotFoundException;
import pl.ing.trading.integration.ExchangeClient;
import pl.ing.trading.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradingServiceTest {

    @Mock
    private ExchangeClient exchangeClient;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private TradingProperties tradingProperties;

    @Mock
    private MarketDataService marketDataService;

    @InjectMocks
    private TradingService tradingService;

    @Test
    void buyOrder_shouldSaveOrderWithSubmittedStatus() {
        when(tradingProperties.getAccountId()).thenReturn(123L);
        when(marketDataService.findTickerByIsin("PLBSK0000017"))
                .thenReturn(new TickersResponse("ING Bank Śląski", "INGBSK", "PLBSK0000017", "PLN", "XWAR"));
        when(exchangeClient.submitOrder(any()))
                .thenReturn(new OrderSubmitResponse(1111111L, OrderStatus.SUBMITTED, 1762444418L));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        BuyOrderRequest request = new BuyOrderRequest("PLBSK0000017", 10, OrderType.LMT, new BigDecimal("320"), null);
        BuyOrderResponse response = tradingService.buyOrder(request);

        assertEquals(1111111L, response.orderId());
        assertEquals(OrderStatus.SUBMITTED, response.status());

        ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepository).save(captor.capture());
        OrderEntity saved = captor.getValue();
        assertEquals(OrderStatus.SUBMITTED, saved.getStatus());
        assertEquals("PLBSK0000017", saved.getIsin());
        assertEquals("INGBSK", saved.getTicker());
        assertEquals(10, saved.getQuantity());
        assertEquals("XWAR", saved.getMic());
    }

    @Test
    void getOrderDetails_shouldReturnDetailsWithCommission() {
        OrderEntity entity = OrderEntity.builder()
                .orderId(1111111L)
                .status(OrderStatus.FILLED)
                .isin("PLBSK0000017")
                .ticker("INGBSK")
                .tradeCurrency("PLN")
                .executionPrice(new BigDecimal("315"))
                .quantity(10)
                .registrationTime(1762444418L)
                .executedTime(1762448027L)
                .mic("XWAR")
                .build();
        when(orderRepository.findByOrderId(1111111L)).thenReturn(Optional.of(entity));

        OrderDetailsResponse details = tradingService.getOrderDetails(1111111L);

        assertEquals(OrderStatus.FILLED, details.status());
        assertEquals(new BigDecimal("3150"), details.orderValue());
        assertEquals(new BigDecimal("9.45"), details.commission());
        assertNotNull(details.registrationTime());
        assertNotNull(details.executedTime());
    }

    @Test
    void getOrderDetails_shouldThrowWhenOrderNotFound() {
        when(orderRepository.findByOrderId(999L)).thenReturn(Optional.empty());

        assertThrows(OrderDetailsNotFoundException.class, () -> tradingService.getOrderDetails(999L));
    }
}

