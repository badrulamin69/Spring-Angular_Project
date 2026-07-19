package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionWaitingList;
import com.badrulamin.University_Management.entity.AdmissionWaitingListEntry;
import com.badrulamin.University_Management.service.AdmissionWaitingListService;
import com.badrulamin.University_Management.service.AdmissionWaitingListEntryService;
import com.badrulamin.University_Management.payload.response.PagedResponse;
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
import java.util.Map;

@RestController
@RequestMapping("/api/admission-waiting-lists")
@RequiredArgsConstructor
public class AdmissionWaitingListController {

    private final AdmissionWaitingListService waitingListService;
    private final AdmissionWaitingListEntryService entryService;

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
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false) Long testId) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdmissionWaitingList> paged = waitingListService.findByFilters(search, status, sessionId,
                facultyId, programId, testId, pageable);
        return ResponseEntity.ok(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(),
                paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<AdmissionWaitingList> findById(@PathVariable Long id) {
        return ResponseEntity.ok(waitingListService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionWaitingList> save(@Valid @RequestBody AdmissionWaitingList waitingList) {
        return ResponseEntity.ok(waitingListService.save(waitingList));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionWaitingList> update(@PathVariable Long id, @Valid @RequestBody AdmissionWaitingList waitingList) {
        return ResponseEntity.ok(waitingListService.update(id, waitingList));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        waitingListService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionWaitingList> generate(
            @RequestParam Long testId,
            @RequestParam(required = false) String listName,
            @RequestParam(required = false) Integer totalSlots,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) Long facultyId,
            @RequestParam(required = false) Long programId) {
        return ResponseEntity.ok(waitingListService.generateWaitingList(testId, listName, totalSlots,
                academicYear, facultyId, programId));
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionWaitingList> publish(@PathVariable Long id) {
        return ResponseEntity.ok(waitingListService.publish(id));
    }

    @PutMapping("/{id}/unpublish")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionWaitingList> unpublish(@PathVariable Long id) {
        return ResponseEntity.ok(waitingListService.unpublish(id));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(waitingListService.getStats());
    }

    @GetMapping("/{waitingListId}/entries")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<?> getEntries(
            @PathVariable Long waitingListId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "rank") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdmissionWaitingListEntry> paged = entryService.findByFilters(waitingListId, search, status, pageable);
        return ResponseEntity.ok(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(),
                paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast()));
    }

    @GetMapping("/{waitingListId}/entries/all")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<List<AdmissionWaitingListEntry>> getAllEntries(@PathVariable Long waitingListId) {
        return ResponseEntity.ok(entryService.findByWaitingListIdOrdered(waitingListId));
    }

    @PostMapping("/{waitingListId}/entries")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionWaitingListEntry> addEntry(@PathVariable Long waitingListId,
            @RequestBody AdmissionWaitingListEntry entry) {
        entry.setWaitingList(waitingListService.findById(waitingListId));
        return ResponseEntity.ok(entryService.save(entry));
    }

    @PutMapping("/entries/{entryId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionWaitingListEntry> updateEntry(@PathVariable Long entryId,
            @RequestBody AdmissionWaitingListEntry entry) {
        return ResponseEntity.ok(entryService.update(entryId, entry));
    }

    @PutMapping("/entries/{entryId}/status")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmissionWaitingListEntry> updateEntryStatus(@PathVariable Long entryId,
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
