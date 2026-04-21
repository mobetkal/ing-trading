package pl.ing.trading.dto;

import pl.ing.trading.enums.OrderType;

import java.math.BigDecimal;

public record BuyOrderRequest(
        String isin,
        Integer quantity,
        OrderType orderType,
        BigDecimal limitPrice,
        Long expiresAt
) {
}

