package dev.tanmay.contactmanagementsystem.controller;

import dev.tanmay.contactmanagementsystem.dto.request.StatusUpdateRequest;
import dev.tanmay.contactmanagementsystem.dto.response.AdminContactResponseDTO;
import dev.tanmay.contactmanagementsystem.dto.response.ApiResponse;
import dev.tanmay.contactmanagementsystem.service.StatusWorkFlowService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/contacts")
public class StatusWorkFlowController {

    private final StatusWorkFlowService statusWorkFlowService;

    public StatusWorkFlowController(
            StatusWorkFlowService statusWorkFlowService) {
        this.statusWorkFlowService = statusWorkFlowService;
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Object>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateRequest dto
    ) {
        log.info("Received request to update status of contact with id {}", id);

        AdminContactResponseDTO responseDTO =
                statusWorkFlowService.updateStatus(id, dto);

        return ResponseEntity.ok(
                ApiResponse.success("Status changed", responseDTO)
        );
    }
}