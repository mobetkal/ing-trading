package pl.ing.trading.dto;

import pl.ing.trading.enums.OrderSide;
import pl.ing.trading.enums.OrderStatus;

import java.math.BigDecimal;

public record OrderStatusResponse(
        Long orderId,
        OrderStatus status,
        String isin,
        OrderSide side,
        String tradeCurrency,
        Integer quantity,
        BigDecimal executionPrice,
        Long registrationTime,
        Long executedTime
) {
}
