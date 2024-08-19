package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.requests.EmailRequest;
import com.devsuperior.dscatalog.services.exceptions.EmailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServices {

    @Value("${spring.mail.username}")
    private String emailFrom;

    private final JavaMailSender mailSender;

    public EmailServices(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(EmailRequest obj) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(obj.getTo());
            message.setSubject(obj.getSubject());
            message.setText(obj.getBody());
            mailSender.send(message);
        }
        catch (MailException e){
            throw new EmailException("Failed to send email");
        }
    }
}
