package com.notificationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;

import com.notificationservice.configs.MailConfig;
import com.notificationservice.dtos.NotificationMessageDTO;

@Service
@Primary
public class EmailNotificationService implements SendNotificationService{

    @Autowired
    private MailConfig config;

    @Override
    public void SendNotification(NotificationMessageDTO notificationMessage) {
        Session session = config.javaMailSender();

        dispatchEmail(session, notificationMessage.getTo(), notificationMessage.getSubject(), notificationMessage.getBody());

    }

    private static void dispatchEmail(Session mailSession, String recipient, String mailSubject, String mailBody){
        try
        {
            MimeMessage email = new MimeMessage(mailSession);

            email.addHeader("Content-type", "text/HTML; charset=UTF-8");
            email.addHeader("format", "flowed");
            email.addHeader("Content-Transfer-Encoding", "8bit");

            email.setFrom(new InternetAddress("ScalerSaurabhCapstoneDemo@gmail.com", "Team Scaler"));

            email.setReplyTo(InternetAddress.parse("ScalerSaurabhCapstoneDemo@gmail.com", false));

            email.setSubject(mailSubject, "UTF-8");

            email.setText(mailBody, "UTF-8");

            email.setSentDate(new Date());

            email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));
            System.out.println("Email prepared for sending..");
            Transport.send(email);

            System.out.println("Notification email delivered successfully");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}