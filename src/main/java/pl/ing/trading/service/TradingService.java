package pl.ing.trading.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ing.trading.config.TradingProperties;
import pl.ing.trading.dto.BuyOrderRequest;
import pl.ing.trading.dto.BuyOrderResponse;
import pl.ing.trading.dto.OrderDetailsResponse;
import pl.ing.trading.dto.OrderListItem;
import pl.ing.trading.dto.OrderRequest;
import pl.ing.trading.enums.OrderSide;
import pl.ing.trading.dto.OrderSubmitResponse;
import pl.ing.trading.dto.TickersResponse;
import pl.ing.trading.entity.OrderEntity;
import pl.ing.trading.exceptions.OrderDetailsNotFoundException;
import pl.ing.trading.integration.ExchangeClient;
import pl.ing.trading.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradingService {

    private final ExchangeClient exchangeClient;
    private final OrderRepository orderRepository;
    private final TradingProperties tradingProperties;
    private final MarketDataService marketDataService;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.of("Europe/Warsaw"));

    public BuyOrderResponse buyOrder(BuyOrderRequest request) {
        TickersResponse ticker = marketDataService.findTickerByIsin(request.isin());

        OrderRequest gpwRequest = new OrderRequest(
                tradingProperties.getAccountId(),
                request.isin(),
                OrderSide.BUY,
                ticker.tradeCurrency(),
                request.quantity(),
                request.expiresAt(),
                request.orderType(),
                request.limitPrice()
        );

        OrderSubmitResponse response = exchangeClient.submitOrder(gpwRequest);

        OrderEntity entity = OrderEntity.builder()
                .orderId(response.orderId())
                .accountId(tradingProperties.getAccountId())
                .isin(request.isin())
                .ticker(ticker.ticker())
                .side(OrderSide.BUY)
                .tradeCurrency(ticker.tradeCurrency())
                .quantity(request.quantity())
                .orderType(request.orderType())
                .limitPrice(request.limitPrice())
                .expiresAt(request.expiresAt())
                .status(response.status())
                .registrationTime(response.registrationTime())
                .mic(ticker.mic())
                .build();

        orderRepository.save(entity);
        log.info("Order saved with orderId: {}", response.orderId());

        return new BuyOrderResponse(response.orderId(), response.status());
    }

    public List<OrderListItem> getOrders() {
        return orderRepository.findByAccountId(tradingProperties.getAccountId())
                .stream()
                .map(e -> new OrderListItem(
                        e.getOrderId(),
                        e.getStatus(),
                        e.getIsin(),
                        e.getQuantity(),
                        e.getLimitPrice()
                ))
                .toList();
    }

    public OrderDetailsResponse getOrderDetails(Long orderId) {
        OrderEntity entity = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new OrderDetailsNotFoundException("Order not found: " + orderId));

        BigDecimal orderValue = null;
        BigDecimal commission = null;
        String executedTimeStr = null;

        if (entity.getExecutionPrice() != null) {
            orderValue = entity.getExecutionPrice().multiply(BigDecimal.valueOf(entity.getQuantity()));
            commission = CommissionCalculator.calculate(entity.getMic(), orderValue);
        }

        if (entity.getExecutedTime() != null) {
            executedTimeStr = DATE_FORMATTER.format(Instant.ofEpochSecond(entity.getExecutedTime()));
        }

        String registrationTimeStr = entity.getRegistrationTime() != null
                ? DATE_FORMATTER.format(Instant.ofEpochSecond(entity.getRegistrationTime()))
                : null;

        return new OrderDetailsResponse(
                entity.getOrderId(),
                entity.getStatus(),
                entity.getIsin(),
                entity.getTicker(),
                entity.getTradeCurrency(),
                entity.getExecutionPrice(),
                entity.getQuantity(),
                orderValue,
                registrationTimeStr,
                executedTimeStr,
                commission
        );
    }
}

