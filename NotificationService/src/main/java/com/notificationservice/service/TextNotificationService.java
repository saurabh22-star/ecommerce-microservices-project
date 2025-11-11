package com.notificationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.notificationservice.configs.TwilioConfig;
import com.notificationservice.dtos.NotificationMessageDTO;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class TextNotificationService implements SendNotificationService{

    @Autowired
    private TwilioConfig twilioConfig;


    @Override
    public void SendNotification(NotificationMessageDTO notificationMessage) {
        String recipientPhoneNumber = notificationMessage.getNumber().toString(); 

        String messageBody = "Hi, "+notificationMessage.getSubject()+" "+notificationMessage.getBody();

        Message message = Message.creator(
                new PhoneNumber(recipientPhoneNumber), 
                new PhoneNumber(twilioConfig.getPhoneNumber()),  
                messageBody                           
        ).create();

        System.out.println("Message successfully sent. Message SID: " + message.getSid());
    }
}

