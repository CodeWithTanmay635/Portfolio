package dev.tanmay.contactmanagementsystem.controller;

import dev.tanmay.contactmanagementsystem.service.AdminService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(
            AdminService adminService
    ){
        this.adminService = adminService;
    }
}
