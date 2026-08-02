package dev.tanmay.contactmanagementsystem.service;

import dev.tanmay.contactmanagementsystem.dto.request.StatusUpdateRequest;
import dev.tanmay.contactmanagementsystem.dto.response.AdminContactResponseDTO;
import dev.tanmay.contactmanagementsystem.dto.response.ApiResponse;
import dev.tanmay.contactmanagementsystem.exception.ContactNotFoundException;
import dev.tanmay.contactmanagementsystem.exception.InvalidStatusTransitionException;
import dev.tanmay.contactmanagementsystem.model.AuditLog;
import dev.tanmay.contactmanagementsystem.model.Contact;
import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import dev.tanmay.contactmanagementsystem.repository.AuditLogRepository;
import dev.tanmay.contactmanagementsystem.repository.ContactRepository;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
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
    public AdminContactResponseDTO updateStatus (UUID id, StatusUpdateRequest dto) {
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

    private void applyStatusChange(
            Contact contact,
            MessageStatus newStatus
    ){
        MessageStatus oldStatus = contact.getStatus();
        contact.setStatus(newStatus);

        if(oldStatus == MessageStatus.REPLIED) {
            contact.markReplied();
        }

        log.info("Status Changed Successfully of ID : {} | {} -> {}",contact.getId(), oldStatus, newStatus);
    }

    private void saveContact(Contact contact) {
        contactRepository.save(contact);
    }

    private void createAuditLog(Contact contact,
                                MessageStatus newStatus,
                                String note) {
            String actor = SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();

            auditLogRepository.save(AuditLog.of(
                    contact.getId(),
                    contact.getStatus(),
                    newStatus,
                    actor,
                    note
            ));
    }

    private void publishIfReplied(Contact saved){
        if(saved.getStatus() == MessageStatus.REPLIED){
            eventPublisher.publishEvent(
                    new ContactReceivedEvent(this, saved));
                log.info("Replied Successfully of ID : {}",saved.getId());
        }
    }


}