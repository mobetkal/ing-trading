package pl.ing.trading;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.ing.trading.dto.OrderSubmitResponse;
import pl.ing.trading.dto.TickersResponse;
import pl.ing.trading.entity.OrderEntity;
import pl.ing.trading.enums.OrderSide;
import pl.ing.trading.enums.OrderStatus;
import pl.ing.trading.enums.OrderType;
import pl.ing.trading.integration.ExchangeClient;
import pl.ing.trading.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class TradingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoBean
    private ExchangeClient exchangeClient;

    @Test
    void shouldSubmitOrderAndSaveToDb() throws Exception {
        when(exchangeClient.getTickers()).thenReturn(List.of(
                new TickersResponse("ING Bank Śląski", "INGBSK", "PLBSK0000017", "PLN", "XWAR")));
        when(exchangeClient.submitOrder(any())).thenReturn(
                new OrderSubmitResponse(222222L, OrderStatus.SUBMITTED, 1762444418L));

        String body = """
                {"isin":"PLBSK0000017","quantity":10,"orderType":"LMT","limitPrice":320,"expiresAt":null}
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(222222))
                .andExpect(jsonPath("$.status").value("Submitted"));
    }

    @Test
    void shouldReturnOrdersList() throws Exception {
        orderRepository.save(OrderEntity.builder()
                .orderId(333333L)
                .accountId(123L)
                .isin("PLBSK0000017")
                .ticker("INGBSK")
                .side(OrderSide.BUY)
                .tradeCurrency("PLN")
                .quantity(5)
                .orderType(OrderType.LMT)
                .limitPrice(new BigDecimal("300"))
                .status(OrderStatus.SUBMITTED)
                .registrationTime(1762444418L)
                .mic("XWAR")
                .build());

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.orderId==333333)].isin", contains("PLBSK0000017")));
    }

    @Test
    void shouldReturnFilledOrderDetailsWithCommission() throws Exception {
        orderRepository.save(OrderEntity.builder()
                .orderId(444444L)
                .accountId(123L)
                .isin("PLBSK0000017")
                .ticker("INGBSK")
                .side(OrderSide.BUY)
                .tradeCurrency("PLN")
                .quantity(10)
                .orderType(OrderType.LMT)
                .limitPrice(new BigDecimal("320"))
                .status(OrderStatus.FILLED)
                .executionPrice(new BigDecimal("315"))
                .registrationTime(1762444418L)
                .executedTime(1762448027L)
                .mic("XWAR")
                .build());

        mockMvc.perform(get("/api/orders/444444"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(444444))
                .andExpect(jsonPath("$.status").value("Filled"))
                .andExpect(jsonPath("$.orderValue").value(3150))
                .andExpect(jsonPath("$.commission").value(9.45))
                .andExpect(jsonPath("$.registrationTime").isNotEmpty())
                .andExpect(jsonPath("$.executedTime").isNotEmpty());
    }
}

