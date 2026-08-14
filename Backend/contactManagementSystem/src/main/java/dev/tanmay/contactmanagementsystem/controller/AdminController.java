package dev.tanmay.contactmanagementsystem.controller;

import dev.tanmay.contactmanagementsystem.dto.response.AdminContactResponseDTO;
import dev.tanmay.contactmanagementsystem.dto.response.ApiResponse;
import dev.tanmay.contactmanagementsystem.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(
            AdminService adminService
    ){
        this.adminService = adminService;
    }
    @GetMapping
    public ResponseEntity<ApiResponse<AdminContactResponseDTO>> getAllContacts(
            @RequestBody AdminContactResponseDTO dto,
            HttpServletResponse response){
        log.info("Getting all contacts from admin");

        AdminContactResponseDTO responseDTO = adminService.getAllContacts(
                20,
                20,
                "all contacts"
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        ""
                ))
    }
}
