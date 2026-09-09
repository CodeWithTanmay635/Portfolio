package dev.tanmay.contactmanagementsystem.service;

import dev.tanmay.contactmanagementsystem.dto.request.StatusUpdateRequest;
import dev.tanmay.contactmanagementsystem.exception.InvalidStatusTransitionException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void shouldRejectInvalidStatusTransition() {

        Contact contact = Contact.builder()
                .name("Test User")
                .email("test2@example.com")
                .subject("Test Subject")
                .message("Test message")
                .referenceId("TEST-002")
                .status(MessageStatus.ARCHIVED)
                .build();

        contactRepository.save(contact);

        StatusUpdateRequest request = new StatusUpdateRequest(
                MessageStatus.REPLIED,
                "Trying invalid transition."
        );

        assertThrows(
                InvalidStatusTransitionException.class,
                () -> statusWorkFlowService.updateStatus(
                        contact.getId(),
                        request
                )
        );
    }
}