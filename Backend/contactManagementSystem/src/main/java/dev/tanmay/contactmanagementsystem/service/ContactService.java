package dev.tanmay.contactmanagementsystem.service;
import dev.tanmay.contactmanagementsystem.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final EmailService emailService;

    @Autowired
    public ContactService(ContactRepository contactRepository, EmailService emailService) {
        this.contactRepository = contactRepository;
        this.emailService = emailService;
    }
}

//Constructor injection for ContactRepository — same DI pattern you already know.
// Spring sees ContactService needs a ContactRepository, finds the generated bean, injects it.
//getMessageOrThrow as a private helper — every admin action (markAsRead, archive, delete)
// needs to find a message by id first, and all of them should fail the same way if it doesn't exist.
// One helper, reused everywhere, avoids duplicated if (message == null) checks scattered across methods.
//MessageNotFoundException — this is new, and it's the next file we need.
// findById() returns Optional<ContactMessage> because the message might not exist.
// .orElseThrow() is how you handle that "might not exist" case cleanly — throw a meaningful exception
// instead of risking a NullPointerException later.
//Why delete() checks existence first — contactRepository.deleteById(id) on a non-existent id
// either silently does nothing or throws a low-level Spring exception with a bad error message.
// Checking first means you control the error and give a clean 404 instead.
//No status logic duplicated — markAsRead, archive,
// markAsReplied all follow the identical pattern: fetch → change status → save.
// This is intentionally simple right now. (Later, when you study AOP,
// you'll see how to log every one of these transitions automatically without repeating logging code
// in each method — but that's deferred, as agreed.)