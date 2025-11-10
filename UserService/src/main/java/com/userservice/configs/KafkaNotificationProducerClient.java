package com.userservice.configs;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.userservice.dtos.SendNotificationMessageDTO;

@Component
public class KafkaNotificationProducerClient {
    private static final String TOPIC = "notification-topic";
    private final KafkaTemplate<String, SendNotificationMessageDTO> kafkaTemplate;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(KafkaNotificationProducerClient.class);


    public KafkaNotificationProducerClient(KafkaTemplate<String, SendNotificationMessageDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishNotiificationEvent(SendNotificationMessageDTO sendNotificationMessageDTO) {
        kafkaTemplate.send(TOPIC, sendNotificationMessageDTO);
        logger.info("Notification event published: {}", sendNotificationMessageDTO);
    }
}