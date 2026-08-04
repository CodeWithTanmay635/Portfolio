package dev.tanmay.contactmanagementsystem.service;

import dev.tanmay.contactmanagementsystem.model.Contact;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ContactRepliedEvent extends ApplicationEvent {
    private final Contact saved;
    public ContactRepliedEvent(Object source, Contact saved) {
        super(source);
        this.saved = saved;
    }
}
