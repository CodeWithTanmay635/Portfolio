package dev.tanmay.contactmanagementsystem.listeners;

import dev.tanmay.contactmanagementsystem.event.ContactReceivedEvent;
import dev.tanmay.contactmanagementsystem.event.ContactRepliedEvent;
import dev.tanmay.contactmanagementsystem.model.Contact;
import dev.tanmay.contactmanagementsystem.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContactEventListener {

    private final EmailService emailService;

    public ContactEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @EventListener
    public void handleContactReceived(ContactReceivedEvent event) {
        // get contact from event
        Contact contact = event.getContact();
        // send acknowledgement email
        emailService.sendAcknowledgement(contact);
        // log
        log.info("Received contact with id {} subject: {}", contact.getId(), contact.getMessage());
    }

    @Async
    @EventListener
    public void handleContactReplied(ContactRepliedEvent event) {
        // get contact from event
        Contact contact = event.getSaved();
        // log replied
        log.info("Reply processed — contact ID: {}", contact.getId());
        // future — dashboard websocket update
    }
}
