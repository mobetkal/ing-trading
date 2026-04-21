package pl.ing.trading.dto;

public record TickersResponse(
        String name,
        String ticker,
        String isin,
        String tradeCurrency,
        String mic
) {
}
