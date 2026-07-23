package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.StudentFee;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.StudentFeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-fees")
@RequiredArgsConstructor
public class StudentFeeController {

    private final StudentFeeService studentFeeService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    public ResponseEntity<ApiResponse<PagedResponse<StudentFee>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StudentFee> paged = studentFeeService.findAll(pageable);
        PagedResponse<StudentFee> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    public ResponseEntity<ApiResponse<StudentFee>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(studentFeeService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    public ResponseEntity<ApiResponse<StudentFee>> save(@Valid @RequestBody StudentFee studentFee) {
        return ResponseEntity.ok(ApiResponse.success(studentFeeService.save(studentFee)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    public ResponseEntity<ApiResponse<StudentFee>> update(@PathVariable Long id, @Valid @RequestBody StudentFee studentFee) {
        return ResponseEntity.ok(ApiResponse.success(studentFeeService.update(id, studentFee)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        studentFeeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
