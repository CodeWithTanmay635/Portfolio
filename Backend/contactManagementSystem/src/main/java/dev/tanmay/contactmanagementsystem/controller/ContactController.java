package dev.tanmay.contactmanagementsystem.controller;

import dev.tanmay.contactmanagementsystem.service.ContactService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(
            ContactService contactService
    ) {
        this.contactService = contactService;
    }
    @PostMapping
    public String submitContact() {
        return "Contact API Working!";
    }

}