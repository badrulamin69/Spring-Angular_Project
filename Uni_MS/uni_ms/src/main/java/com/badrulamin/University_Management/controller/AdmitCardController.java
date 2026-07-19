package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmitCard;
import com.badrulamin.University_Management.service.AdmitCardService;
import com.badrulamin.University_Management.service.AdmitCardPdfService;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admit-cards")
@RequiredArgsConstructor
public class AdmitCardController {

    private final AdmitCardService admitCardService;
    private final AdmitCardPdfService admitCardPdfService;

    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<AdmitCard>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdmitCard> paged = admitCardService.findAll(pageable);
        PagedResponse<AdmitCard> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<AdmitCard> findById(@PathVariable Long id) {
        return ResponseEntity.ok(admitCardService.findById(id));
    }

    @GetMapping("/test/{testId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<List<AdmitCard>> findByTestId(@PathVariable Long testId) {
        return ResponseEntity.ok(admitCardService.findByTestId(testId));
    }

    @GetMapping("/registration/{registrationId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<List<AdmitCard>> findByRegistrationId(@PathVariable Long registrationId) {
        return ResponseEntity.ok(admitCardService.findByRegistrationId(registrationId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmitCard> save(@Valid @RequestBody AdmitCard admitCard) {
        return ResponseEntity.ok(admitCardService.save(admitCard));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<AdmitCard> update(@PathVariable Long id, @Valid @RequestBody AdmitCard admitCard) {
        return ResponseEntity.ok(admitCardService.update(id, admitCard));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        admitCardService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generate/{testId}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<List<AdmitCard>> generateAdmitCards(@PathVariable Long testId) {
        return ResponseEntity.ok(admitCardService.generateAdmitCards(testId));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<byte[]> downloadAdmitCardPdf(@PathVariable Long id) {
        AdmitCard admitCard = admitCardService.findById(id);
        byte[] pdfBytes = admitCardPdfService.generateAdmitCardPdf(admitCard);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "admit-card-" + admitCard.getAdmitCardNumber() + ".pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
