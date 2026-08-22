package dev.tanmay.contactmanagementsystem.controller;

import dev.tanmay.contactmanagementsystem.dto.response.AdminContactResponseDTO;
import dev.tanmay.contactmanagementsystem.dto.response.ApiResponse;
import dev.tanmay.contactmanagementsystem.dto.response.PagedResponse;
import dev.tanmay.contactmanagementsystem.model.enums.MessagePriority;
import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import dev.tanmay.contactmanagementsystem.service.AdminService;
import dev.tanmay.contactmanagementsystem.service.ReplyService;
import dev.tanmay.contactmanagementsystem.service.StatusWorkFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("api/v1/admin/contacts")
public class AdminController {
    private final AdminService adminService;
    private final ReplyService replyService;
    private final StatusWorkFlowService status;

    public AdminController(
            AdminService adminService,
            ReplyService replyService,
            StatusWorkFlowService status
    ){
        this.adminService = adminService;
        this.replyService = replyService;
        this.status = status;
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<PagedResponse<AdminContactResponseDTO>>> get(
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

}
