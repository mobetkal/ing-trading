package pl.ing.trading.dto;

import pl.ing.trading.enums.OrderStatus;

public record OrderSubmitResponse(
        Long orderId,
        OrderStatus status,
        Long registrationTime
) {
}

