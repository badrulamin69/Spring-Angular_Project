package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionCampaign;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.AdmissionCampaignService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admission-campaigns")
public class AdmissionCampaignController {

    private final AdmissionCampaignService admissionCampaignService;

    public AdmissionCampaignController(AdmissionCampaignService admissionCampaignService) {
        this.admissionCampaignService = admissionCampaignService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<AdmissionCampaign> campaigns = admissionCampaignService.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        return ResponseEntity.ok(Map.of(
                "content", campaigns.getContent(),
                "totalElements", campaigns.getTotalElements(),
                "totalPages", campaigns.getTotalPages(),
                "currentPage", campaigns.getNumber()
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<ApiResponse<AdmissionCampaign>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(admissionCampaignService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_CREATE')")
    public ResponseEntity<ApiResponse<AdmissionCampaign>> create(@Valid @RequestBody AdmissionCampaign campaign) {
        return ResponseEntity.ok(ApiResponse.success(admissionCampaignService.create(campaign)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_EDIT')")
    public ResponseEntity<ApiResponse<AdmissionCampaign>> update(@PathVariable Long id, @Valid @RequestBody AdmissionCampaign campaign) {
        return ResponseEntity.ok(ApiResponse.success(admissionCampaignService.update(id, campaign)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        admissionCampaignService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(Map.of(
                "total", admissionCampaignService.countByStatus("ACTIVE") + admissionCampaignService.countByStatus("COMPLETED") + admissionCampaignService.countByStatus("DRAFT"),
                "active", admissionCampaignService.countByStatus("ACTIVE"),
                "completed", admissionCampaignService.countByStatus("COMPLETED"),
                "draft", admissionCampaignService.countByStatus("DRAFT")
        ));
    }
}
