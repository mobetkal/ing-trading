package pl.ing.trading.entity;

import jakarta.persistence.*;
import lombok.*;
import pl.ing.trading.enums.OrderSide;
import pl.ing.trading.enums.OrderStatus;
import pl.ing.trading.enums.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", unique = true, nullable = false)
    private Long orderId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private String isin;

    @Column
    private String ticker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderSide side;

    @Column(name = "trade_currency", nullable = false)
    private String tradeCurrency;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrderType orderType;

    @Column(name = "limit_price")
    private BigDecimal limitPrice;

    @Column(name = "expires_at")
    private Long expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "execution_price")
    private BigDecimal executionPrice;

    @Column(name = "registration_time")
    private Long registrationTime;

    @Column(name = "executed_time")
    private Long executedTime;

    @Column(nullable = false)
    private String mic;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

