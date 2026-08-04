package dev.tanmay.contactmanagementsystem.event;

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