package pl.ing.trading.dto;

import java.math.BigDecimal;

public record PricesResponse(
        String isin,
        BigDecimal price
) {
}
