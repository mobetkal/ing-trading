package pl.ing.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ing.trading.enums.OrderStatus;
import pl.ing.trading.entity.OrderEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByAccountId(Long accountId);

    Optional<OrderEntity> findByOrderId(Long orderId);

    List<OrderEntity> findByStatus(OrderStatus status);
}

