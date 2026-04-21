package pl.ing.trading.integration;

import pl.ing.trading.dto.*;

import java.util.List;

public interface ExchangeClient {

    List<TickersResponse> getTickers();

    List<PricesResponse> getPrices();

    OrderSubmitResponse submitOrder(OrderRequest request);

    OrderStatusResponse getOrderStatus(Long orderId);
}

