package dev.tanmay.contactmanagementsystem.service;

import dev.tanmay.contactmanagementsystem.dto.request.ReplyRequest;
import dev.tanmay.contactmanagementsystem.dto.response.AdminContactResponseDTO;
import dev.tanmay.contactmanagementsystem.event.ContactRepliedEvent;
import dev.tanmay.contactmanagementsystem.exception.ContactNotFoundException;
import dev.tanmay.contactmanagementsystem.exception.InvalidReplyException;
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

@Service
@Slf4j
public class ReplyService {

    private final EmailService mailService;
    private final ContactRepository contactRepository;
    private final AuditLogRepository auditLogRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ReplyService(
            EmailService mailService,
            ContactRepository contactRepository,
            AuditLogRepository auditLogRepository,
            ApplicationEventPublisher eventPublisher) {
        this.mailService = mailService;
        this.contactRepository = contactRepository;
        this.auditLogRepository = auditLogRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public AdminContactResponseDTO replyToContact(
            UUID id,
            ReplyRequest  dto
    ){
        Contact contact = findContact(id);
        MessageStatus oldStatus = contact.getStatus();
        validateReply(contact);
        mailService.sendReply(contact, dto);
        contact.markReplied();
        Contact saved = contactRepository.save(contact);
        createAuditLog(saved,oldStatus,dto.message());
        publishEvent(saved);
        return  buildResponse(saved);
    }

    //---------------------------- Helper Methods ----------------------------//
    private Contact findContact(UUID id) {
        return contactRepository
                .findById(id).orElseThrow(() ->
                       new ContactNotFoundException(id));
    }

    private void validateReply(Contact contact) {
        if(!MessageStatus.REPLYABLE_STATUSES.contains(contact.getStatus())){
            throw new InvalidReplyException(MessageStatus.REPLYABLE_STATUSES.toString());
        }
    }

   private void createAuditLog(Contact contact,
                               MessageStatus status,
                               String subject){
        String actor = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        auditLogRepository.save(AuditLog.of (
                contact.getId(),
                status,
                MessageStatus.REPLIED,
                actor,
                "Reply Sent subject :" + subject
        ));
   }

   private void publishEvent(Contact saved){
        eventPublisher.publishEvent(
                new ContactRepliedEvent(this, saved)
        );
       log.info("Reply event published for contact {}", saved.getReferenceId());   }

   private AdminContactResponseDTO buildResponse(Contact saved){
        return AdminContactResponseDTO.from(saved);
   }
}
