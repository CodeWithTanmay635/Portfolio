package dev.tanmay.contactmanagementsystem.service;

import dev.tanmay.contactmanagementsystem.dto.request.StatusUpdateRequest;
import dev.tanmay.contactmanagementsystem.dto.response.ApiResponse;
import dev.tanmay.contactmanagementsystem.repository.AuditLogRepository;
import dev.tanmay.contactmanagementsystem.repository.ContactRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StatusWorkFlowService {

    private final ContactRepository contactRepository;
    private final AuditLogRepository auditLogRepository;
    private final ApplicationEventPublisher eventPublisher;

    public StatusWorkFlowService(
            ContactRepository contactRepository,
            AuditLogRepository auditLogRepository,
            ApplicationEventPublisher eventPublisher) {
        this.contactRepository = contactRepository;
        this.auditLogRepository = auditLogRepository;
        this.eventPublisher = eventPublisher;
    }

    public ApiResponse<Void> updateStatus(
            UUID contactId,
            StatusUpdateRequest request
    ){
     return  null;
    }
}
