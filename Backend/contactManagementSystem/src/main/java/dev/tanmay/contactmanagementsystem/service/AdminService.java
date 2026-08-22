package dev.tanmay.contactmanagementsystem.service;

import dev.tanmay.contactmanagementsystem.dto.response.AdminContactResponseDTO;
import dev.tanmay.contactmanagementsystem.dto.response.PagedResponse;
import dev.tanmay.contactmanagementsystem.model.enums.MessagePriority;
import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import dev.tanmay.contactmanagementsystem.repository.ContactRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ConcurrentModificationException;
import java.util.UUID;

@Slf4j
@Service
public class AdminService {

    private final ContactRepository contactRepository;

    public AdminService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public PagedResponse<AdminContactResponseDTO> getAllContacts(
            MessageStatus status,
            MessagePriority priority,
            Pageable pageable) {

        var page = (status != null && priority != null)
                ? contactRepository.findByStatusAndPriority(status, priority, pageable)
                : (status != null)
                  ? contactRepository.findByStatus(status, pageable)
                  : (priority != null)
                    ? contactRepository.findByPriority(priority, pageable)
                    : contactRepository.findAll(pageable);

        return PagedResponse.from(
                page.map(AdminContactResponseDTO::from));
    }

    public AdminContactResponseDTO getById(UUID id){
        return contactRepository.findById(id)
                .map(AdminContactResponseDTO :: from)
                .orElseThrow(() -> new ConcurrentModificationException(id.toString())
                );
        }
}