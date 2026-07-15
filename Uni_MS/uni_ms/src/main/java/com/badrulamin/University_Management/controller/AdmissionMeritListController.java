package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionMeritList;
import com.badrulamin.University_Management.service.AdmissionMeritListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.badrulamin.University_Management.payload.response.PagedResponse;

@RestController
@RequestMapping("/api/admission-merit-lists")
@RequiredArgsConstructor
public class AdmissionMeritListController {

    private final AdmissionMeritListService meritListService;

    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<AdmissionMeritList>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdmissionMeritList> paged = meritListService.findAll(pageable);
        PagedResponse<AdmissionMeritList> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<AdmissionMeritList> findById(@PathVariable Long id) {
        return ResponseEntity.ok(meritListService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionMeritList> save(@Valid @RequestBody AdmissionMeritList meritList) {
        return ResponseEntity.ok(meritListService.save(meritList));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionMeritList> update(@PathVariable Long id, @Valid @RequestBody AdmissionMeritList meritList) {
        return ResponseEntity.ok(meritListService.update(id, meritList));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        meritListService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
