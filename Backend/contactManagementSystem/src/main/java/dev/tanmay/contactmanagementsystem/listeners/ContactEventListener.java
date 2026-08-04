package dev.tanmay.contactmanagementsystem.listeners;

import dev.tanmay.contactmanagementsystem.event.ContactReceivedEvent;
import dev.tanmay.contactmanagementsystem.event.ContactRepliedEvent;
import dev.tanmay.contactmanagementsystem.service.EmailService;
import dev.tanmay.contactmanagementsystem.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContactEventListener {

    private final NotificationService notificationService;
    private final EmailService emailService;

    public ContactEventListener(
                                NotificationService notificationService,
                                EmailService emailService) {
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Async
    @EventListener
    public void handleContactReceived(ContactReceivedEvent event) {

    }

    @Async
    @EventListener
    public void handleContactReplied(ContactRepliedEvent event) {

    }
}
