package pl.ing.trading.dto;

import pl.ing.trading.enums.OrderStatus;

import java.math.BigDecimal;

public record OrderDetailsResponse(
        Long orderId,
        OrderStatus status,
        String isin,
        String ticker,
        String tradeCurrency,
        BigDecimal executionPrice,
        Integer quantity,
        BigDecimal orderValue,
        String registrationTime,
        String executedTime,
        BigDecimal commission
) {
}

