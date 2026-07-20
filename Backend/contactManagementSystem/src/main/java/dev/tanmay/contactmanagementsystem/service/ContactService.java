package dev.tanmay.contactmanagementsystem.service;
import dev.tanmay.contactmanagementsystem.dto.request.ContactRequestDTO;
import dev.tanmay.contactmanagementsystem.dto.response.ContactResponseDTO;
import dev.tanmay.contactmanagementsystem.repository.ContactRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final EmailService emailService;

    @Autowired
    public ContactService(ContactRepository contactRepository, EmailService emailService) {
        this.contactRepository = contactRepository;
        this.emailService = emailService;
    }

    @Transactional
    public ContactResponseDTO save(ContactRequestDTO dto) {
        if(dto.website() != null && !dto.website().isBlank()){
            //do not throw exception - bot learns to retry
            //return fake success - bot thinks it worked
            log.warn("Bot submission detected");
            return buildFakeResponse();
        }
        // save contact
        // save audit log
        // both commit together
        // one fails → both rollback
    }
}