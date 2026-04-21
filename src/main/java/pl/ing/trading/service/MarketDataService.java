package pl.ing.trading.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.ing.trading.dto.PricesResponse;
import pl.ing.trading.dto.TickersResponse;
import pl.ing.trading.exceptions.TickerNotFoundException;
import pl.ing.trading.integration.ExchangeClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataService {

    private final ExchangeClient exchangeClient;

    public List<TickersResponse> getTickers() {
        log.info("Fetching tickers from exchange");
        return exchangeClient.getTickers();
    }

    @Cacheable("prices")
    public List<PricesResponse> getPrices() {
        log.info("Fetching prices from exchange (cache miss)");
        return exchangeClient.getPrices();
    }

    public TickersResponse findTickerByIsin(String isin) {
        return getTickers().stream()
                .filter(t -> t.isin().equals(isin))
                .findFirst()
                .orElseThrow(() -> new TickerNotFoundException("Ticker not found for ISIN: " + isin));
    }
}

