package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.StudentPromotion;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.StudentPromotionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/student-promotions")
public class StudentPromotionController {

    private final StudentPromotionService studentPromotionService;

    public StudentPromotionController(StudentPromotionService studentPromotionService) {
        this.studentPromotionService = studentPromotionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<StudentPromotion> items = studentPromotionService.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        Map<String, Object> response = new HashMap<>();
        response.put("content", items.getContent());
        response.put("totalElements", items.getTotalElements());
        response.put("totalPages", items.getTotalPages());
        response.put("currentPage", items.getNumber());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<ApiResponse<StudentPromotion>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(studentPromotionService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<StudentPromotion>> create(@RequestBody StudentPromotion studentPromotion) {
        return ResponseEntity.ok(ApiResponse.success(studentPromotionService.create(studentPromotion)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<StudentPromotion>> update(@PathVariable Long id, @RequestBody StudentPromotion studentPromotion) {
        return ResponseEntity.ok(ApiResponse.success(studentPromotionService.update(id, studentPromotion)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        studentPromotionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPromoted", studentPromotionService.countByStatus("PROMOTED"));
        stats.put("totalPending", studentPromotionService.countByStatus("PENDING"));
        stats.put("totalRejected", studentPromotionService.countByStatus("REJECTED"));
        stats.put("totalDeferred", studentPromotionService.countByStatus("DEFERRED"));
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
