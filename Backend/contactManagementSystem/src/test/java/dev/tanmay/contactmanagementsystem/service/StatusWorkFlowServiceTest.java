package dev.tanmay.contactmanagementsystem.service;

import dev.tanmay.contactmanagementsystem.dto.request.StatusUpdateRequest;

import dev.tanmay.contactmanagementsystem.exception.ContactNotFoundException;
import dev.tanmay.contactmanagementsystem.exception.InvalidStatusTransitionException;
import dev.tanmay.contactmanagementsystem.model.AuditLog;
import dev.tanmay.contactmanagementsystem.model.Contact;
import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import dev.tanmay.contactmanagementsystem.repository.AuditLogRepository;
import dev.tanmay.contactmanagementsystem.repository.ContactRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import dev.tanmay.contactmanagementsystem.dto.response.AdminContactResponseDTO;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StatusWorkFlowServiceTest {
    @Autowired
    private StatusWorkFlowService statusWorkFlowService;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @MockitoBean
    private JavaMailSender javaMailSender;


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldCreateExactlyOneAuditLogForStatusChange() {

        Contact contact = Contact.builder()
                .name("Test User")
                .email("test16@example.com")
                .subject("Test Subject")
                .message("Test message")
                .referenceId("TEST-016")
                .status(MessageStatus.NEW)
                .build();

        contactRepository.save(contact);

        StatusUpdateRequest request = new StatusUpdateRequest(
                MessageStatus.READ,
                "Marking contact as read."
        );

        statusWorkFlowService.updateStatus(
                contact.getId(),
                request
        );

        List<AuditLog> logs =
                auditLogRepository.findAllByContactIdOrderByChangedAtAsc(
                        contact.getId()
                );

        assertEquals(1, logs.size());

        assertEquals(
                MessageStatus.NEW,
                logs.get(0).getOldStatus()
        );

        assertEquals(
                MessageStatus.READ,
                logs.get(0).getNewStatus()
        );
    }
}