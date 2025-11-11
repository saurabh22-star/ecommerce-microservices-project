package com.notificationservice.consumers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.notificationservice.dtos.NotificationMessageDTO;
import com.notificationservice.service.SendNotificationService;

@Service
public class NotificationEventConsumer {

    @Autowired
    private SendNotificationService sendNotificationService;

    @KafkaListener(topics = "notification-topic", groupId = "notification-service-group")
    public void handleSendEmailEvent(NotificationMessageDTO notificationMessageDTO) {
        sendNotificationService.SendNotification(notificationMessageDTO);
  }
}
