package dev.tanmay.contactmanagementsystem.service;

import dev.tanmay.contactmanagementsystem.model.Contact;
import org.springframework.context.ApplicationEvent;

public class ContactRepliedEvent extends ApplicationEvent {
    private final Contact saved;
    public ContactRepliedEvent(Object source, Contact saved) {
        super(source);
        this.saved = saved;
    }

    public Contact getSaved() {return saved;}
}
