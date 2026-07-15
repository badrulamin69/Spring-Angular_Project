package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionOfferLetter;
import com.badrulamin.University_Management.service.AdmissionOfferLetterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admission-offer-letters")
public class AdmissionOfferLetterController {

    private final AdmissionOfferLetterService admissionOfferLetterService;

    public AdmissionOfferLetterController(AdmissionOfferLetterService admissionOfferLetterService) {
        this.admissionOfferLetterService = admissionOfferLetterService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<AdmissionOfferLetter> letters = admissionOfferLetterService.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        return ResponseEntity.ok(Map.of(
                "content", letters.getContent(),
                "totalElements", letters.getTotalElements(),
                "totalPages", letters.getTotalPages(),
                "currentPage", letters.getNumber()
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<AdmissionOfferLetter> findById(@PathVariable Long id) {
        return ResponseEntity.ok(admissionOfferLetterService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_CREATE')")
    public ResponseEntity<AdmissionOfferLetter> create(@Valid @RequestBody AdmissionOfferLetter offerLetter) {
        return ResponseEntity.ok(admissionOfferLetterService.create(offerLetter));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_EDIT')")
    public ResponseEntity<AdmissionOfferLetter> update(@PathVariable Long id, @Valid @RequestBody AdmissionOfferLetter offerLetter) {
        return ResponseEntity.ok(admissionOfferLetterService.update(id, offerLetter));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        admissionOfferLetterService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/accept")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionOfferLetter> accept(@PathVariable Long id) {
        return ResponseEntity.ok(admissionOfferLetterService.accept(id));
    }

    @PutMapping("/{id}/decline")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionOfferLetter> decline(@PathVariable Long id, @Valid @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(admissionOfferLetterService.decline(id, body.getOrDefault("reason", "")));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(Map.of(
                "total", admissionOfferLetterService.countByStatus("ISSUED") + admissionOfferLetterService.countByStatus("ACCEPTED") + admissionOfferLetterService.countByStatus("DECLINED"),
                "issued", admissionOfferLetterService.countByStatus("ISSUED"),
                "accepted", admissionOfferLetterService.countByStatus("ACCEPTED"),
                "declined", admissionOfferLetterService.countByStatus("DECLINED")
        ));
    }
}
