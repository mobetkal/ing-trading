package pl.ing.trading.dto;

import pl.ing.trading.enums.OrderSide;
import pl.ing.trading.enums.OrderType;

import java.math.BigDecimal;

public record OrderRequest(
        Long accountId,
        String isin,
        OrderSide side,
        String tradeCurrency,
        Integer quantity,
        Long expiresAt,
        OrderType orderType,
        BigDecimal limitPrice
) {
}
