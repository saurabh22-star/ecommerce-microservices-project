package com.notificationservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationMessageDTO {
    private String to;
    private String from;
    private String subject;
    private String body;
    private Long number;
}
