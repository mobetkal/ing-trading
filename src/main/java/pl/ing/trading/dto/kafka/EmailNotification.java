package pl.ing.trading.dto.kafka;

public record EmailNotification(
        String topic,
        String message
) {
}
