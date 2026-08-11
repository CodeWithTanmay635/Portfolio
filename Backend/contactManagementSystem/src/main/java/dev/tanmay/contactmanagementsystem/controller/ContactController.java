package dev.tanmay.contactmanagementsystem.controller;

import dev.tanmay.contactmanagementsystem.service.AdminService;
import dev.tanmay.contactmanagementsystem.service.ContactService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contact")
public class ContactController {

    private final ContactService contactService;
    private final AdminService adminService;

    public ContactController(
            ContactService contactService,
            AdminService adminService
    ) {
        this.contactService = contactService;
        this.adminService = adminService;
    }
    @PostMapping
    public String submitContact() {
        return "Contact API Working!";
    }

}