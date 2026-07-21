package dev.tanmay.contactmanagementsystem.service;
import dev.tanmay.contactmanagementsystem.dto.request.ContactRequestDTO;
import dev.tanmay.contactmanagementsystem.dto.response.ContactResponseDTO;
import dev.tanmay.contactmanagementsystem.exception.DuplicateContactException;
import dev.tanmay.contactmanagementsystem.model.AuditLog;
import dev.tanmay.contactmanagementsystem.model.Contact;
import dev.tanmay.contactmanagementsystem.model.enums.MessagePriority;
import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import dev.tanmay.contactmanagementsystem.repository.ContactRepository;
import dev.tanmay.contactmanagementsystem.repository.AuditLogRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


@Slf4j
@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final AuditLogRepository auditLogRepository;
    private final PriorityEstimatorService priorityEstimatorService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public  ContactService(ContactRepository contactRepository, AuditLogRepository auditLogRepository,  PriorityEstimatorService priorityEstimatorService, ApplicationEventPublisher applicationEventPublisher) {
        this.contactRepository = contactRepository;
        this.auditLogRepository = auditLogRepository;
        this.priorityEstimatorService = priorityEstimatorService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public ContactResponseDTO save(ContactRequestDTO dto) {
        if(dto.website() != null && !dto.website().isBlank()){
            //do not throw exception - bot learns to retry
            //return fake success - bot thinks it worked
            log.warn("Bot submission detected");
            return buildFakeResponse();
        }

        boolean isDuplicate = contactRepository
                .existsByEmailAndCreatedAtAfter(
                        dto.email(),
                        Instant.now().minus(10, ChronoUnit.MINUTES)
                );
        if(isDuplicate){
            throw new DuplicateContactException(dto.email());

        }

        Contact contact = Contact.builder()
                .name(dto.name().trim())
                .email(dto.email().toLowerCase().trim())
                .message(dto.message().trim())
               //.userAgent(userAgent)
                .build();

        int score = priorityEstimatorService.calculateScore(dto);
        contact.setPriority(MessagePriority.fromScore(score));
        contact.setPriorityScore(score);

        Contact saved = contactRepository.save(contact);

        auditLogRepository.save(AuditLog.of(
                saved.getId(),
                null,
                MessageStatus.NEW,
                "SYSTEM",
                "Initial submission"
        ));

        applicationEventPublisher.publishEvent(
                new ContactReceivedEvent(this, saved)
        );

        return ContactResponseDTO.from(contact);
    }

    private ContactResponseDTO buildFakeResponse(){
        return ContactResponseDTO.from(
                Contact.builder()
                        .id(UUID.randomUUID())
                        .createdAt(Instant.now())
                        .build()
        );
    }
}