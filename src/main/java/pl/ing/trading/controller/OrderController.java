package pl.ing.trading.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ing.trading.dto.OrderDetailsResponse;
import pl.ing.trading.dto.OrderListItem;
import pl.ing.trading.service.TradingService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final TradingService tradingService;

    @GetMapping
    public ResponseEntity<List<OrderListItem>> getOrders() {
        return ResponseEntity.ok(tradingService.getOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailsResponse> getOrderDetails(@PathVariable Long orderId) {
        return ResponseEntity.ok(tradingService.getOrderDetails(orderId));
    }
}

