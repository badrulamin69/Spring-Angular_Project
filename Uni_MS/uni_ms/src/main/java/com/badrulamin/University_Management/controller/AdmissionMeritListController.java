package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionMeritList;
import com.badrulamin.University_Management.entity.AdmissionMeritListEntry;
import com.badrulamin.University_Management.service.AdmissionMeritListService;
import com.badrulamin.University_Management.service.AdmissionMeritListEntryService;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admission-merit-lists")
@RequiredArgsConstructor
public class AdmissionMeritListController {

    private final AdmissionMeritListService meritListService;
    private final AdmissionMeritListEntryService entryService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long sessionId,
            @RequestParam(required = false) Long facultyId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false) Long testId) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdmissionMeritList> paged = meritListService.findByFilters(search, status, sessionId,
                facultyId, departmentId, programId, testId, pageable);
        return ResponseEntity.ok(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(),
                paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<AdmissionMeritList> findById(@PathVariable Long id) {
        return ResponseEntity.ok(meritListService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionMeritList> save(@Valid @RequestBody AdmissionMeritList meritList) {
        return ResponseEntity.ok(meritListService.save(meritList));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionMeritList> update(@PathVariable Long id, @Valid @RequestBody AdmissionMeritList meritList) {
        return ResponseEntity.ok(meritListService.update(id, meritList));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        meritListService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionMeritList> generate(
            @RequestParam Long testId,
            @RequestParam(required = false) String listName,
            @RequestParam(required = false) Integer totalSeats,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) Long facultyId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false) String shift) {
        return ResponseEntity.ok(meritListService.generateMeritList(testId, listName, totalSeats,
                academicYear, facultyId, departmentId, programId, shift));
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionMeritList> publish(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(meritListService.publish(id, userDetails.getUsername()));
    }

    @PutMapping("/{id}/unpublish")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionMeritList> unpublish(@PathVariable Long id) {
        return ResponseEntity.ok(meritListService.unpublish(id));
    }

    @PutMapping("/{id}/archive")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionMeritList> archive(@PathVariable Long id) {
        return ResponseEntity.ok(meritListService.archive(id));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(meritListService.getStats());
    }

    @GetMapping("/{meritListId}/entries")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<?> getEntries(
            @PathVariable Long meritListId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "rank") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String quotaType) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdmissionMeritListEntry> paged = entryService.findByFilters(meritListId, search, status, quotaType, pageable);
        return ResponseEntity.ok(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(),
                paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast()));
    }

    @GetMapping("/{meritListId}/entries/all")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<List<AdmissionMeritListEntry>> getAllEntries(@PathVariable Long meritListId) {
        return ResponseEntity.ok(entryService.findByMeritListIdOrdered(meritListId));
    }

    @PostMapping("/{meritListId}/entries")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionMeritListEntry> addEntry(@PathVariable Long meritListId,
            @RequestBody AdmissionMeritListEntry entry) {
        entry.setMeritList(meritListService.findById(meritListId));
        return ResponseEntity.ok(entryService.save(entry));
    }

    @PutMapping("/entries/{entryId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionMeritListEntry> updateEntry(@PathVariable Long entryId,
            @RequestBody AdmissionMeritListEntry entry) {
        return ResponseEntity.ok(entryService.update(entryId, entry));
    }

    @PutMapping("/entries/{entryId}/status")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionMeritListEntry> updateEntryStatus(@PathVariable Long entryId,
            @RequestParam String status) {
        return ResponseEntity.ok(entryService.updateStatus(entryId, status));
    }

    @DeleteMapping("/entries/{entryId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long entryId) {
        entryService.delete(entryId);
        return ResponseEntity.noContent().build();
    }
}
