package dev.tanmay.contactmanagementsystem.service;

import dev.tanmay.contactmanagementsystem.dto.request.ReplyRequest;
import dev.tanmay.contactmanagementsystem.model.Contact;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.notification.email}")
    private String notificationEmail; // YOUR email, where you receive alerts

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendReply(Contact contact, ReplyRequest replyRequest) {
        log.info("Sending email to contact with id {} subject: {}", contact.getId(), contact.getMessage());
    }
    public void sendNewMessageNotification(String visitorName, String visitorEmail, String visitorMessage) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(notificationEmail);
        mail.setSubject("New Contact Message from " + visitorName);
        mail.setText(
                "You received a new message on your portfolio.\n\n" +
                        "Name: " + visitorName + "\n" +
                        "Email: " + visitorEmail + "\n" +
                        "Message:\n" + visitorMessage
        );
        mailSender.send(mail);
    }
}