package pl.ing.trading.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.ing.trading.enums.OrderStatus;
import pl.ing.trading.dto.OrderStatusResponse;
import pl.ing.trading.dto.kafka.EmailNotification;
import pl.ing.trading.entity.OrderEntity;
import pl.ing.trading.integration.ExchangeClient;
import pl.ing.trading.repository.OrderRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatusPollingService {

    private final OrderRepository orderRepository;
    private final ExchangeClient exchangeClient;
    private final NotificationService notificationService;

    @Scheduled(fixedDelay = 5000)
    public void pollPendingOrders() {
        List<OrderEntity> pendingOrders = orderRepository.findByStatus(OrderStatus.SUBMITTED);
        if (pendingOrders.isEmpty()) {
            return;
        }

        log.debug("Polling {} pending orders", pendingOrders.size());

        for (OrderEntity order : pendingOrders) {
            try {
                OrderStatusResponse orderStatus = exchangeClient.getOrderStatus(order.getOrderId());
                if (orderStatus == null) continue;

                OrderStatus newStatus = orderStatus.status();
                if (newStatus != order.getStatus()) {
                    order.setStatus(newStatus);

                    if (OrderStatus.FILLED == newStatus) {
                        order.setExecutionPrice(orderStatus.executionPrice());
                        order.setExecutedTime(orderStatus.executedTime());
                        orderRepository.save(order);

                        log.info("Order {} filled at price {}", order.getOrderId(), orderStatus.executionPrice());

                        notificationService.addEmailNotificationTask(new EmailNotification(
                                "Zlecenie zrealizowane",
                                String.format("Zlecenie %d na %s (%d szt.) zostało zrealizowane po cenie %s %s",
                                        order.getOrderId(),
                                        order.getIsin(),
                                        order.getQuantity(),
                                        orderStatus.executionPrice(),
                                        order.getTradeCurrency())
                        ));
                    } else {
                        orderRepository.save(order);
                        log.info("Order {} status changed to {}", order.getOrderId(), newStatus);
                    }
                }
            } catch (Exception e) {
                log.error("Error polling order {}: {}", order.getOrderId(), e.getMessage());
            }
        }
    }
}

