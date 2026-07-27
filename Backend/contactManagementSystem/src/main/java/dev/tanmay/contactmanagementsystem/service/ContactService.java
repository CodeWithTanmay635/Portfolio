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
        public  ContactService(ContactRepository contactRepository,
                               AuditLogRepository auditLogRepository,
                               PriorityEstimatorService priorityEstimatorService,
                               ApplicationEventPublisher applicationEventPublisher) {
            this.contactRepository = contactRepository;
            this.auditLogRepository = auditLogRepository;
            this.priorityEstimatorService = priorityEstimatorService;
            this.applicationEventPublisher = applicationEventPublisher;
        }

        @Transactional
        public ContactResponseDTO submitContact(ContactRequestDTO dto) {

            //validate honeypot
            ContactResponseDTO fakeResponse = validateHoneyPot(dto);
            if(fakeResponse != null){
                return  fakeResponse;
            }

            //validates duplicate submissions
            validateDuplicateSubmission(dto);

            //builds contact
            Contact contact = buildContact(dto);

            //assigns priority
            assignPriority(contact,dto);

            //saves contact
            Contact saved = savedContact(contact);

            //creating Audit trails
            createAuditLog(saved, contact.getIpAddress());


            applicationEventPublisher.publishEvent(
                    new ContactReceivedEvent(this, saved)
            );

            return ContactResponseDTO.from(saved);
        }

        private ContactResponseDTO buildFakeResponse(){
            return new  ContactResponseDTO(
                    UUID.randomUUID(),
                    "Message received. will response shortly.",
                    Instant.now()
            );
        }

        private ContactResponseDTO validateHoneyPot(ContactRequestDTO dto){
            if(dto.website() != null && !dto.website().isBlank()){
                log.warn("Honey pot triggered for email= {}", dto.website());
                return buildFakeResponse();
            }
            return  null;
        }

        private void validateDuplicateSubmission(ContactRequestDTO dto){
            boolean isDuplicate = contactRepository
                    .existsByEmailAndCreatedAtAfter(
                            dto.email().toLowerCase().trim(),
                            Instant.now().minus(10, ChronoUnit.MINUTES)
                    );
            if(isDuplicate){
                throw new DuplicateContactException(dto.email());
            }
        }

        private Contact buildContact(ContactRequestDTO dto){
            return Contact.builder()
                    .referenceId(generateReferenceId())
                    .name(dto.name().trim())
                    .email(dto.email().toLowerCase().trim())
                    .message(dto.message().trim())
                    .status(MessageStatus.NEW)
                    .build();
        }

        private String generateReferenceId(){
            return "CNT-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
        }

        private void assignPriority(Contact contact,
                                    ContactRequestDTO dto){
                int score = priorityEstimatorService.calculateScore(dto);
                contact.setPriority(MessagePriority.fromScore(score));
                contact.setPriorityScore(score);
        }

        private Contact savedContact(Contact contact){
            Contact saved = contactRepository.save(contact);
            log.info("Saved Contact -- ID {}, Priority {}",
                    saved.getId(),
                    saved.getPriority());
            return saved;
        }

        private void createAuditLog(Contact contact , String ipAddress){
                auditLogRepository.save(AuditLog.of(
                        contact.getId(),
                        null,
                        MessageStatus.NEW,
                        "SYSTEM",
                        "Initial submission"+ ipAddress
                ));
        }
        private boolean looksLikeMessage(String text) {
            return text.length() > 40
                    || text.contains(".")
                    || text.contains("please")
                    || text.contains("website")
                    || text.contains("project")
                    || text.contains("help");
        }
    }