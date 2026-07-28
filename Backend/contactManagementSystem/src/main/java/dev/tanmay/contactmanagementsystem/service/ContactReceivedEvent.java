package dev.tanmay.contactmanagementsystem.service;

import dev.tanmay.contactmanagementsystem.model.Contact;
import org.springframework.context.ApplicationEvent;

public class ContactReceivedEvent extends ApplicationEvent {

    private final Contact contact;

    public ContactReceivedEvent(Object source, Contact contact) {
        super(source);
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }
}