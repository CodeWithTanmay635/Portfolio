package dev.tanmay.contactmanagementsystem.service;

import dev.tanmay.contactmanagementsystem.dto.request.StatusUpdateRequest;
import dev.tanmay.contactmanagementsystem.dto.response.AdminContactResponseDTO;
import dev.tanmay.contactmanagementsystem.dto.response.ApiResponse;
import dev.tanmay.contactmanagementsystem.exception.ContactNotFoundException;
import dev.tanmay.contactmanagementsystem.exception.InvalidStatusTransitionException;
import dev.tanmay.contactmanagementsystem.model.Contact;
import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import dev.tanmay.contactmanagementsystem.repository.AuditLogRepository;
import dev.tanmay.contactmanagementsystem.repository.ContactRepository;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StatusWorkFlowService {

    private final ContactRepository contactRepository;
    private final AuditLogRepository auditLogRepository;
    private final ApplicationEventPublisher eventPublisher;

    public StatusWorkFlowService(
            ContactRepository contactRepository,
            AuditLogRepository auditLogRepository,
            ApplicationEventPublisher eventPublisher) {
        this.contactRepository = contactRepository;
        this.auditLogRepository = auditLogRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public AdminContactResponseDTO updateStatus(
            UUID id,
            StatusUpdateRequest dto) {
        return null;
    }

    //---------------------Helper Methods-------------------------

    private Contact findContact(UUID id) {
        return contactRepository
                .findById(id).orElseThrow(() ->
                        new ContactNotFoundException(id));
    }

    private void validateTransaction(Contact contact,
                                     MessageStatus newStatus) {
        if(!contact.getStatus().canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(contact);
        }
    }
}