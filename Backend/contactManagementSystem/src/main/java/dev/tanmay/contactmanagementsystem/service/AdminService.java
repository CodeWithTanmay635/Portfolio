package dev.tanmay.contactmanagementsystem.service;

import dev.tanmay.contactmanagementsystem.dto.response.AdminContactResponseDTO;
import dev.tanmay.contactmanagementsystem.model.Contact;
import dev.tanmay.contactmanagementsystem.repository.ContactRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminService {

    private final ContactRepository contactRepository;

    public AdminService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public Page<AdminContactResponseDTO> getAllContacts(
            int pageNumber, int size, String sortBy) {
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(sortBy));

        return contactRepository.findAll(pageable)
                .map(AdminContactResponseDTO :: from);
    }
}
