package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.CourseRegistration;
import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.payload.request.AdvisorApprovalRequest;
import com.badrulamin.University_Management.payload.response.AdvisorApprovalResponse;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.repository.UserRepository;
import com.badrulamin.University_Management.service.AdvisorApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advisor-approvals")
@RequiredArgsConstructor
public class AdvisorApprovalController {

    private final AdvisorApprovalService advisorApprovalService;
    private final UserRepository userRepository;

    @GetMapping("/pending/{semesterId}")
    @PreAuthorize("hasAuthority('REGISTRATION_VIEW')")
    public ResponseEntity<ApiResponse<List<CourseRegistration>>> getPendingApprovals(@PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(advisorApprovalService.getPendingApprovals(semesterId)));
    }

    @PostMapping("/process")
    @PreAuthorize("hasAuthority('REGISTRATION_APPROVE')")
    public ResponseEntity<ApiResponse<AdvisorApprovalResponse>> processApproval(
            @Valid @RequestBody AdvisorApprovalRequest request,
            Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("User not found"));
        }
        return ResponseEntity.ok(ApiResponse.success("Approval processed successfully",
                advisorApprovalService.processApproval(request, user.getId())));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('REGISTRATION_APPROVE')")
    public ResponseEntity<ApiResponse<List<AdvisorApprovalResponse>>> processBulkApproval(
            @RequestBody List<Long> studentIds,
            @RequestParam Long semesterId,
            @RequestParam String action,
            @RequestParam(required = false) String comments,
            Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("User not found"));
        }
        return ResponseEntity.ok(ApiResponse.success("Bulk approval processed",
                advisorApprovalService.processBulkApproval(studentIds, semesterId, action, comments, user.getId())));
    }
}
