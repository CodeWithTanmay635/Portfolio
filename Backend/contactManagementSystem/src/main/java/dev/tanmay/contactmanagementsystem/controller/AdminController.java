package dev.tanmay.contactmanagementsystem.controller;

import dev.tanmay.contactmanagementsystem.dto.request.ReplyRequest;
import dev.tanmay.contactmanagementsystem.dto.request.StatusUpdateRequest;
import dev.tanmay.contactmanagementsystem.dto.response.AdminContactResponseDTO;
import dev.tanmay.contactmanagementsystem.dto.response.ApiResponse;
import dev.tanmay.contactmanagementsystem.dto.response.PagedResponse;
import dev.tanmay.contactmanagementsystem.model.enums.MessagePriority;
import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import dev.tanmay.contactmanagementsystem.service.AdminService;
import dev.tanmay.contactmanagementsystem.service.ReplyService;
import dev.tanmay.contactmanagementsystem.service.StatusWorkFlowService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("api/v1/admin/contacts")
public class AdminController {
    private final AdminService adminService;
    private final ReplyService replyService;
    private final StatusWorkFlowService statusWorkFlowService;

    public AdminController(
            AdminService adminService,
            ReplyService replyService,
            StatusWorkFlowService statusWorkFlowService
    ){
        this.adminService = adminService;
        this.replyService = replyService;
        this.statusWorkFlowService = statusWorkFlowService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AdminContactResponseDTO>>> getAllMessages(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(required = false)MessageStatus status,
        @RequestParam(required = false)MessagePriority priority
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        PagedResponse<AdminContactResponseDTO> response =
                adminService.getAllContacts(
                        status,
                        priority,
                        pageable
                );
        return ResponseEntity.ok(
                ApiResponse.success("Messages fetched", response)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminContactResponseDTO>> getById(
            @PathVariable UUID id){
        AdminContactResponseDTO response = adminService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Message fetched", response)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AdminContactResponseDTO>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateRequest dto
            ){
        log.info("Status update - ID: {} -> {}", id, dto.newStatus());

        AdminContactResponseDTO response =
                statusWorkFlowService.updateStatus(id,dto);

        return ResponseEntity.ok(
                ApiResponse.success("Message updated", response)
        );
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<ApiResponse<AdminContactResponseDTO>> reply(
            @PathVariable UUID id,
            @Valid @RequestBody ReplyRequest dto
    ){
        log.info("Reply - sent: {}", id);

        AdminContactResponseDTO response =
                replyService.replyToContact(id, dto);

        return ResponseEntity.ok(
                ApiResponse.success("Message replied", response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id){
        log.info("Delete - ID: {}", id);
        adminService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success("Message deleted", null)
        );
    }

}
