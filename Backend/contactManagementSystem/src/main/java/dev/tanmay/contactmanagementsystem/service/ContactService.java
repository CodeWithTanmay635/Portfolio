package dev.tanmay.contactmanagementsystem.service;


import dev.tanmay.contactmanagementsystem.dto.ContactRequest;
import dev.tanmay.contactmanagementsystem.dto.ContactResponse;
import dev.tanmay.contactmanagementsystem.exception.MessageNotFoundException;
import dev.tanmay.contactmanagementsystem.model.ContactMessage;
import dev.tanmay.contactmanagementsystem.model.MessageStatus;
import dev.tanmay.contactmanagementsystem.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final EmailService emailService;

    @Autowired
    public ContactService(ContactRepository contactRepository, EmailService emailService) {
        this.contactRepository = contactRepository;
        this.emailService = emailService;
    }


    public ContactResponse submitContact(ContactRequest request) {
        ContactMessage message = toEntity(request);
        ContactMessage saved = contactRepository.save(message);

        emailService.sendNewMessageNotification(
                saved.getName(),
                saved.getEmail(),
                saved.getMessage()
        );

        return toResponse(saved);
    }


    // Admin: get all messages, newest first
    public List<ContactResponse> getAllMessages() {
        return contactRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Admin: filter by status
    public List<ContactResponse> getMessagesByStatus(MessageStatus status) {
        return contactRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Admin: mark as read
    public ContactResponse markAsRead(Long id) {
        ContactMessage message = getMessageOrThrow(id);
        message.setStatus(MessageStatus.READ);
        return toResponse(contactRepository.save(message)); // @PreUpdate fires here
    }

    // Admin: archive
    public ContactResponse archive(Long id) {
        ContactMessage message = getMessageOrThrow(id);
        message.setStatus(MessageStatus.ARCHIVED);
        return toResponse(contactRepository.save(message));
    }

    // Admin: mark as replied
    public ContactResponse markAsReplied(Long id) {
        ContactMessage message = getMessageOrThrow(id);
        message.setStatus(MessageStatus.REPLIED);
        return toResponse(contactRepository.save(message));
    }

    // Admin: delete
    public void delete(Long id) {
        getMessageOrThrow(id); // ensures it exists before deleting, gives a clean 404 instead of silent no-op
        contactRepository.deleteById(id);
    }

    // ----- Helpers -----

    private ContactMessage getMessageOrThrow(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException(id));
    }

    private ContactMessage toEntity(ContactRequest request) {
        ContactMessage message = new ContactMessage();
        message.setName(request.getName());
        message.setEmail(request.getEmail());
        message.setMessage(request.getMessage());
        return message;
        // status, createdAt, updatedAt are NOT set here — @PrePersist owns that
    }

    private ContactResponse toResponse(ContactMessage entity) {
        return new ContactResponse(
                entity.getId(),
                entity.getMessage(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
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