package com.notificationservice.service;

import com.notificationservice.dtos.NotificationMessageDTO;

public interface SendNotificationService {
    void SendNotification(NotificationMessageDTO notificationMessage);
}
