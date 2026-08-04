package dev.tanmay.contactmanagementsystem.service;

import dev.tanmay.contactmanagementsystem.model.Contact;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ContactReceivedEvent extends ApplicationEvent {

    private final Contact contact;

    public ContactReceivedEvent(Object source, Contact contact) {
        super(source);
        this.contact = contact;
    }
}