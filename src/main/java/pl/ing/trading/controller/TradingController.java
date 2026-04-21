package pl.ing.trading.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ing.trading.dto.BuyOrderRequest;
import pl.ing.trading.dto.BuyOrderResponse;
import pl.ing.trading.dto.PricesResponse;
import pl.ing.trading.dto.TickersResponse;
import pl.ing.trading.service.MarketDataService;
import pl.ing.trading.service.TradingService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TradingController {

    private final MarketDataService marketDataService;
    private final TradingService tradingService;

    @GetMapping("/tickers")
    public ResponseEntity<List<TickersResponse>> getTickers() {
        return ResponseEntity.ok(marketDataService.getTickers());
    }

    @GetMapping("/prices")
    public ResponseEntity<List<PricesResponse>> getPrices() {
        return ResponseEntity.ok(marketDataService.getPrices());
    }

    @PostMapping("/orders")
    public ResponseEntity<BuyOrderResponse> buyOrder(@RequestBody BuyOrderRequest request) {
        return ResponseEntity.ok(tradingService.buyOrder(request));
    }
}

