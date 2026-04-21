package pl.ing.trading.dto;

import pl.ing.trading.enums.OrderStatus;

import java.math.BigDecimal;

public record OrderListItem(
        Long orderId,
        OrderStatus status,
        String isin,
        Integer quantity,
        BigDecimal limitPrice
) {
}

