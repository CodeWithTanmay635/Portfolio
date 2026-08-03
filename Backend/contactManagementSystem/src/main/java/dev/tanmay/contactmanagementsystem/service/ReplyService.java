package dev.tanmay.contactmanagementsystem.service;

import dev.tanmay.contactmanagementsystem.dto.request.ReplyRequest;
import dev.tanmay.contactmanagementsystem.dto.response.AdminContactResponseDTO;
import dev.tanmay.contactmanagementsystem.exception.AlreadyRepliedException;
import dev.tanmay.contactmanagementsystem.exception.ContactNotFoundException;
import dev.tanmay.contactmanagementsystem.exception.InvalidReplyException;
import dev.tanmay.contactmanagementsystem.model.Contact;
import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import dev.tanmay.contactmanagementsystem.repository.AuditLogRepository;
import dev.tanmay.contactmanagementsystem.repository.ContactRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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

    public AdminContactResponseDTO replyToContact(
            UUID id,
            ReplyRequest  replyRequest
    ){
        Contact contact = findContact(id);
        return  null;
    }

    //---------------------------- Helper Methods ----------------------------//
    private Contact findContact(UUID id) {
        return contactRepository
                .findById(id).orElseThrow(() ->
                       new ContactNotFoundException(id));
    }

    private void validateReply(Contact contact, ReplyRequest request) {
        if(contact.getStatus() == MessageStatus.REPLIED){
            throw new AlreadyRepliedException(contact.getReferenceId());
        }
        if(contact.getStatus() == MessageStatus.ARCHIVED){
            throw new InvalidReplyException("Cannot reply message is archived")
        }
    }
}
