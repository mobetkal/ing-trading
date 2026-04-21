package pl.ing.trading.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pl.ing.trading.dto.*;

import java.util.List;

@Slf4j
@Component
public class GpwClient implements ExchangeClient {

    private final RestClient restClient;

    public GpwClient(@Value("${integrations.external.rest.gpw.url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public List<TickersResponse> getTickers() {
        log.debug("Fetching tickers from GPW");
        ResultListResponse<TickersResponse> response = restClient.get()
                .uri("/gpw/tickers")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return response != null ? response.results() : List.of();
    }

    @Override
    public List<PricesResponse> getPrices() {
        log.debug("Fetching prices from GPW");
        ResultListResponse<PricesResponse> response = restClient.get()
                .uri("/gpw/prices/current")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return response != null ? response.results() : List.of();
    }

    @Override
    public OrderSubmitResponse submitOrder(OrderRequest request) {
        log.info("Submitting order to GPW: {}", request);
        return restClient.post()
                .uri("/gpw/orders")
                .body(request)
                .retrieve()
                .body(OrderSubmitResponse.class);
    }

    @Override
    public OrderStatusResponse getOrderStatus(Long orderId) {
        log.debug("Fetching order status from GPW for orderId: {}", orderId);
        return restClient.get()
                .uri("/gpw/order/{id}", orderId)
                .retrieve()
                .body(OrderStatusResponse.class);
    }
}

