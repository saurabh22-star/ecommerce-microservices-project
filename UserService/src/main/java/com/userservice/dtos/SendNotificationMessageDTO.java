package com.userservice.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SendNotificationMessageDTO {
    private String to;
    private String from;
    private String subject;
    private String body;
    private Long number;
}
