package dev.tanmay.contactmanagementsystem.service;
import dev.tanmay.contactmanagementsystem.dto.request.ContactRequestDTO;
import dev.tanmay.contactmanagementsystem.dto.response.ContactResponseDTO;
import dev.tanmay.contactmanagementsystem.model.AuditLog;
import dev.tanmay.contactmanagementsystem.model.Contact;
import dev.tanmay.contactmanagementsystem.model.enums.MessagePriority;
import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import dev.tanmay.contactmanagementsystem.repository.ContactRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static sun.net.www.protocol.http.HttpURLConnection.userAgent;


@Slf4j
@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final EmailService emailService;
    private final AuditLogRepository auditLogRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository, EmailService emailService, AuditLogRepository auditLogRepository) {
        this.contactRepository = contactRepository;
        this.emailService = emailService;
        this.auditLogRepository = auditLogRepository;
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
                .existsByEmailAndCreatedAfter(
                        dto.email(),
                        Instant.now().minus(10, ChronoUnit.MINUTES)
                );
        if(isDuplicate){
            throw new DuplicateContactException(dto.email());

        }

        Contact contact = new Contact.builder()
                .name(dto.name().trim())
                .email(dto.email().toLowerCase().trim())
                .message(dto.message().trim())
                .userAgent(userAgent)
                .build();

        int score = priorityEsimatorService.claculateScore(dto);
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


        // save contact
        // save audit log
        // both commit together
        // one fails → both rollback
    }
}