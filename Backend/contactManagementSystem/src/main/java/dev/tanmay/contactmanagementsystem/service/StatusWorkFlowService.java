package dev.tanmay.contactmanagementsystem.service;

import dev.tanmay.contactmanagementsystem.dto.request.StatusUpdateRequest;
import dev.tanmay.contactmanagementsystem.dto.response.AdminContactResponseDTO;
import dev.tanmay.contactmanagementsystem.exception.ContactNotFoundException;
import dev.tanmay.contactmanagementsystem.exception.InvalidStatusTransitionException;
import dev.tanmay.contactmanagementsystem.model.AuditLog;
import dev.tanmay.contactmanagementsystem.model.Contact;
import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import dev.tanmay.contactmanagementsystem.repository.AuditLogRepository;
import dev.tanmay.contactmanagementsystem.repository.ContactRepository;
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

    public StatusWorkFlowService(
            ContactRepository contactRepository,
            AuditLogRepository auditLogRepository) {
        this.contactRepository = contactRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public AdminContactResponseDTO updateStatus (UUID id, StatusUpdateRequest dto) {

        //Find Contact
        Contact contact = findContact(id);

        //validate contact Transition
        validateTransition(contact, dto.newStatus());

        //capture old status
        MessageStatus oldStatus = contact.getStatus();
        //apply Status
        applyStatusChange(contact, dto.newStatus());

        //save status
        Contact saved = contactRepository.save(contact);

        //create Audit log
        createAuditLog(
                saved,
                oldStatus,
                dto.newStatus(),
                dto.note()
        );

        //build response
        return buildResponse(saved);
    }

    //---------------------Helper Methods-------------------------

    private Contact findContact(UUID id) {
        return contactRepository
                .findById(id).orElseThrow(() ->
                        new ContactNotFoundException(id));
    }

    private void validateTransition(Contact contact,
                                     MessageStatus newStatus) {
        if(!contact.getStatus().canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(contact);
        }
    }

    private void applyStatusChange(
            Contact contact,
            MessageStatus newStatus
    ){
        contact.setStatus(newStatus);

        if(newStatus == MessageStatus.REPLIED) {
            contact.markReplied();
        }

        log.info("Status Changed Successfully of ID : {} -> {}",contact.getId(), newStatus);
    }

    private void createAuditLog(
            Contact contact,
            MessageStatus oldStatus,
            MessageStatus newStatus,
            String note
    ) {
        String actor = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        auditLogRepository.save(
                AuditLog.of(
                        contact.getId(),
                        oldStatus,
                        newStatus,
                        actor,
                        note
                )
        );
    }

    private AdminContactResponseDTO buildResponse(Contact contact) {
        return AdminContactResponseDTO.from(contact);
    }
}