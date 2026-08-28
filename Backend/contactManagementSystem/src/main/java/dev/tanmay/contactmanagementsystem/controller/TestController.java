package dev.tanmay.contactmanagementsystem.controller;

import dev.tanmay.contactmanagementsystem.dto.request.ContactRequestDTO;
import dev.tanmay.contactmanagementsystem.dto.response.ContactResponseDTO;
import dev.tanmay.contactmanagementsystem.service.ContactService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    private final ContactService contactService;

    public TestController(ContactService contactService) {
        this.contactService = contactService;
    }

    // ── 1. health check ────────────────────────────────────────
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ICMS running");
    }

    // ── 2. submit contact — happy path ─────────────────────────
    @PostMapping("/contact")
    public ResponseEntity<ContactResponseDTO> submit(
            @Valid @RequestBody ContactRequestDTO dto,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        log.info("Contact submission from IP: {}", ipAddress);

        ContactResponseDTO response = contactService.submitContact(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── 3. bot simulation — honeypot filled ───────────────────
    @PostMapping("/contact/bot")
    public ResponseEntity<ContactResponseDTO> botSimulation(
            HttpServletRequest request) {

        // website field filled — simulates bot
        ContactRequestDTO botDto = new ContactRequestDTO(
                "Bot Name",
                "bot@spam.com",
                "spam message",
                "",
                "http://spam.com"   // honeypot filled
        );

        ContactResponseDTO response = contactService.submitContact(
                botDto
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── 4. duplicate simulation ────────────────────────────────
    @PostMapping("/contact/duplicate")
    public ResponseEntity<ContactResponseDTO> duplicateSimulation(
            HttpServletRequest request) {

        ContactRequestDTO dto = new ContactRequestDTO(
                "Same Person",
                "same@email.com",
                "First message",
                "",
                ""
        );

        // first submission
        contactService.submitContact(dto);

        // second submission — same email within 10 min
        // this throws DuplicateContactException
        ContactResponseDTO response = contactService.submitContact(
                dto
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── 5. validation failure simulation ──────────────────────
    @PostMapping("/contact/invalid")
    public ResponseEntity<ContactResponseDTO> invalidSimulation(
            HttpServletRequest request) {

        // @Valid will reject this — blank name, invalid email
        ContactRequestDTO dto = new ContactRequestDTO(
                "",              // blank name — fails @NotBlank
                "not-an-email",  // fails @Email,
                "not a subject",
                "hi",            // too short — fails @Size(min=10)
                null
        );

        ContactResponseDTO response = contactService.submitContact(
                dto
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}