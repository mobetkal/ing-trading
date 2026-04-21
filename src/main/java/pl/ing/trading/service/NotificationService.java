package pl.ing.trading.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import pl.ing.trading.dto.kafka.EmailNotification;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void addEmailNotificationTask(EmailNotification notification) {
        kafkaTemplate.send("notifications.email.v1", notification);
    }

    @KafkaListener(topics = "notifications.email.v1", groupId = "ing-trading-group")
    public void sentEmailNotification(@Payload EmailNotification notification) {
        log.info("[EMAIL NOTIFICATION] {}", notification);
    }
}
