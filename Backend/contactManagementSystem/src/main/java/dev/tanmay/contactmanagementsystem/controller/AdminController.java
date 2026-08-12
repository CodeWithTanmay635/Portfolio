package dev.tanmay.contactmanagementsystem.controller;

import dev.tanmay.contactmanagementsystem.dto.request.ContactRequestDTO;
import dev.tanmay.contactmanagementsystem.dto.response.ApiResponse;
import dev.tanmay.contactmanagementsystem.dto.response.ContactResponseDTO;
import dev.tanmay.contactmanagementsystem.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping
    ResponseEntity<ApiResponse<ContactResponseDTO>> submitContact(
            @RequestBody ContactRequestDTO dto,
            HttpServletRequest request){
        log.info
    }

}
