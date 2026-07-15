package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionWaitingList;
import com.badrulamin.University_Management.service.AdmissionWaitingListService;
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
@RequestMapping("/api/admission-waiting-lists")
@RequiredArgsConstructor
public class AdmissionWaitingListController {

    private final AdmissionWaitingListService waitingListService;

    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<AdmissionWaitingList>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdmissionWaitingList> paged = waitingListService.findAll(pageable);
        PagedResponse<AdmissionWaitingList> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<AdmissionWaitingList> findById(@PathVariable Long id) {
        return ResponseEntity.ok(waitingListService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionWaitingList> save(@Valid @RequestBody AdmissionWaitingList waitingList) {
        return ResponseEntity.ok(waitingListService.save(waitingList));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionWaitingList> update(@PathVariable Long id, @Valid @RequestBody AdmissionWaitingList waitingList) {
        return ResponseEntity.ok(waitingListService.update(id, waitingList));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        waitingListService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
