package dev.tanmay.contactmanagementsystem.service;

import dev.tanmay.contactmanagementsystem.dto.request.ReplyRequest;
import dev.tanmay.contactmanagementsystem.model.Contact;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.notification.email}")
    private String adminEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ── visitor gets confirmation ──────────────────────────────
    public void sendAcknowledgement(Contact contact) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(contact.getEmail());
        mail.setSubject("We received your message");
        mail.setText(
                "Hi " + contact.getName() + ",\n\n" +
                        "Thank you for reaching out.\n" +
                        "We have received your message and will get back to you shortly.\n\n" +
                        "Reference ID: " + contact.getId() + "\n\n" +
                        "Regards"
        );
        mailSender.send(mail);
        log.info("Acknowledgement sent — to: {}", contact.getEmail());
    }

    // ── admin gets notification — Phase 2 Telegram replaces ───
    public void sendAdminNotification(Contact contact) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(adminEmail);
        mail.setSubject("New message from " + contact.getName());
        mail.setText(
                "New contact submission.\n\n" +
                        "Name:    " + contact.getName()    + "\n" +
                        "Email:   " + contact.getEmail()   + "\n" +
                        "Message: " + contact.getMessage() + "\n" +
                        "Priority: " + contact.getPriority()
        );
        mailSender.send(mail);
        log.info("Admin notification sent — contact ID: {}",
                contact.getId());
    }

    // ── admin replies to visitor ───────────────────────────────
    public void sendReply(Contact contact, ReplyRequest dto) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(contact.getEmail());
        mail.setSubject(dto.message());
        mail.setText(dto.body());
        mailSender.send(mail);
        log.info("Reply sent — to: {} subject: {}",
                contact.getEmail(), dto.message());
    }
}