package pl.ing.trading.dto;

import pl.ing.trading.enums.OrderStatus;

public record BuyOrderResponse(
        Long orderId,
        OrderStatus status
) {
}

