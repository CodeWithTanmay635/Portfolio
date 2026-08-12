package dev.tanmay.contactmanagementsystem.controller;

import dev.tanmay.contactmanagementsystem.dto.request.ContactRequestDTO;
import dev.tanmay.contactmanagementsystem.dto.response.ApiResponse;
import dev.tanmay.contactmanagementsystem.dto.response.ContactResponseDTO;
import dev.tanmay.contactmanagementsystem.service.ContactService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
    public ResponseEntity<ApiResponse<ContactResponseDTO>> submitContact(
            @RequestBody ContactRequestDTO dto,
            HttpServletRequest request){
        log.info("Received request to submit contact request IP {}",
                request.getRemoteAddr());

        ContactResponseDTO response = contactService.submitContact(
                dto
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Message received successfully",
                        response
                ));
    }

}