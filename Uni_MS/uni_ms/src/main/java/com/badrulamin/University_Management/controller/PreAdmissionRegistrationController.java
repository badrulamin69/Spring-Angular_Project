package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmitCard;
import com.badrulamin.University_Management.entity.PreAdmissionRegistration;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.AdmitCardRepository;
import com.badrulamin.University_Management.service.PreAdmissionRegistrationService;
import com.badrulamin.University_Management.service.AdmitCardPdfService;
import com.badrulamin.University_Management.service.RegistrationPdfService;
import com.badrulamin.University_Management.service.QrCodeService;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PreAdmissionRegistrationController {

    private final PreAdmissionRegistrationService service;
    private final AdmitCardPdfService admitCardPdfService;
    private final RegistrationPdfService registrationPdfService;
    private final QrCodeService qrCodeService;
    private final AdmitCardRepository admitCardRepository;

    @PostMapping(value = "/pre-admission/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("dateOfBirth") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
            @RequestParam("sscGpa") Double sscGpa,
            @RequestParam("sscYear") Integer sscYear,
            @RequestParam("sscBoard") String sscBoard,
            @RequestParam("hscGpa") Double hscGpa,
            @RequestParam("hscYear") Integer hscYear,
            @RequestParam("hscBoard") String hscBoard,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "bloodGroup", required = false) String bloodGroup,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "fatherName", required = false) String fatherName,
            @RequestParam(value = "motherName", required = false) String motherName,
            @RequestParam(value = "guardianPhone", required = false) String guardianPhone,
            @RequestParam(value = "programPreference1", required = false) String programPreference1,
            @RequestParam(value = "programPreference2", required = false) String programPreference2,
            @RequestParam(value = "programPreference3", required = false) String programPreference3,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "signature", required = false) MultipartFile signature
    ) {
        PreAdmissionRegistration registration = new PreAdmissionRegistration();
        registration.setFirstName(firstName);
        registration.setLastName(lastName);
        registration.setEmail(email);
        registration.setDateOfBirth(dateOfBirth);
        registration.setSscGpa(sscGpa);
        registration.setSscYear(sscYear);
        registration.setSscBoard(sscBoard);
        registration.setHscGpa(hscGpa);
        registration.setHscYear(hscYear);
        registration.setHscBoard(hscBoard);
        if (phone != null) registration.setPhone(phone);
        if (gender != null) registration.setGender(gender);
        if (bloodGroup != null) registration.setBloodGroup(bloodGroup);
        if (address != null) registration.setAddress(address);
        if (fatherName != null) registration.setFatherName(fatherName);
        if (motherName != null) registration.setMotherName(motherName);
        if (guardianPhone != null) registration.setGuardianPhone(guardianPhone);
        if (programPreference1 != null) registration.setProgramPreference1(programPreference1);
        if (programPreference2 != null) registration.setProgramPreference2(programPreference2);
        if (programPreference3 != null) registration.setProgramPreference3(programPreference3);

        return ResponseEntity.ok(service.saveWithUserAccount(registration, photo, signature));
    }

    @GetMapping("/pre-admission/register/{registrationNumber}/pdf")
    public ResponseEntity<byte[]> downloadRegistrationPdf(@PathVariable String registrationNumber) {
        PreAdmissionRegistration reg = service.findByRegistrationNumber(registrationNumber);
        byte[] pdf = registrationPdfService.generateRegistrationReceiptPdf(reg);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=registration-" + registrationNumber + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

    @GetMapping("/pre-admission/register/{registrationNumber}/qr-code")
    public ResponseEntity<byte[]> downloadRegistrationQrCode(@PathVariable String registrationNumber) {
        byte[] qrCode = qrCodeService.generateRegistrationQrCode(registrationNumber);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=qr-" + registrationNumber + ".png")
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }

    @GetMapping("/pre-admission/status/{registrationNumber}")
    public ResponseEntity<Map<String, Object>> checkStatus(@PathVariable String registrationNumber) {
        PreAdmissionRegistration reg = service.findByRegistrationNumber(registrationNumber);
        return ResponseEntity.ok(Map.of(
                "registrationNumber", reg.getRegistrationNumber(),
                "status", reg.getStatus(),
                "firstName", reg.getFirstName(),
                "lastName", reg.getLastName()
        ));
    }

    @GetMapping("/pre-admissions")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW') or hasAuthority('PRE_ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_VIEW')")
    public ResponseEntity<PagedResponse<PreAdmissionRegistration>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PreAdmissionRegistration> paged = service.findAll(pageable);
        return ResponseEntity.ok(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast()));
    }

    @GetMapping("/pre-admissions/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW') or hasAuthority('PRE_ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_VIEW')")
    public ResponseEntity<PreAdmissionRegistration> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/pre-admissions/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<PreAdmissionRegistration> update(@PathVariable Long id, @Valid @RequestBody PreAdmissionRegistration registration) {
        return ResponseEntity.ok(service.update(id, registration));
    }

    @DeleteMapping("/pre-admissions/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/pre-admissions/{id}/approve")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<PreAdmissionRegistration> approve(@PathVariable Long id) {
        return ResponseEntity.ok(service.approve(id));
    }

    @PutMapping("/pre-admissions/{id}/reject")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<PreAdmissionRegistration> reject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.reject(id, body.get("remarks")));
    }

    @GetMapping("/pre-admissions/{id}/admit-card")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW') or hasAuthority('PRE_ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_VIEW')")
    public ResponseEntity<String> getAdmitCard(@PathVariable Long id) {
        PreAdmissionRegistration reg = service.findById(id);
        String html = generateAdmitCardHtml(reg);
        return ResponseEntity.ok()
                .header("Content-Type", "text/html")
                .body(html);
    }

    @PostMapping("/pre-admissions/process-merit")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<Map<String, Object>> processMerit() {
        return ResponseEntity.ok(service.processMerit());
    }

    @GetMapping("/pre-admissions/{id}/admit-card/pdf")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW') or hasAuthority('PRE_ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_VIEW')")
    public ResponseEntity<byte[]> getAdmitCardPdf(@PathVariable Long id) {
        PreAdmissionRegistration reg = service.findById(id);
        AdmitCard admitCard = admitCardRepository.findByRegistration_Id(reg.getId()).stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("AdmitCard", "registrationId", reg.getId()));
        byte[] pdf = admitCardPdfService.generateAdmitCardPdf(admitCard);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=admit-card-" + reg.getRegistrationNumber() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

    @GetMapping("/pre-admissions/merit-preview")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<Map<String, Object>> meritPreview() {
        return ResponseEntity.ok(service.getMeritPreview());
    }

    private String generateAdmitCardHtml(PreAdmissionRegistration reg) {
        String seatNo = "SEAT-" + reg.getRegistrationNumber().replace("PRE-ADM-", "");
        return """
            <!DOCTYPE html>
            <html>
            <head>
            <meta charset="UTF-8">
            <title>Admit Card - %s</title>
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                body { font-family: 'Segoe UI', Arial, sans-serif; background: #f1f5f9; display: flex; justify-content: center; padding: 20px; }
                .admit-card { width: 800px; background: #fff; border: 3px solid #1e40af; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 24px rgba(0,0,0,0.1); }
                .header { background: linear-gradient(135deg, #1e40af, #3b82f6); color: #fff; padding: 24px 32px; text-align: center; position: relative; }
                .header h1 { font-size: 24px; margin-bottom: 4px; letter-spacing: 1px; }
                .header h2 { font-size: 16px; font-weight: 400; opacity: 0.9; }
                .header .watermark { position: absolute; top: 50%%; left: 50%%; transform: translate(-50%%, -50%%) rotate(-30deg); font-size: 80px; opacity: 0.06; font-weight: 900; white-space: nowrap; pointer-events: none; }
                .barcode { text-align: center; padding: 12px; background: #f8fafc; border-bottom: 2px dashed #e2e8f0; font-family: monospace; font-size: 28px; letter-spacing: 6px; color: #1e293b; }
                .barcode-label { font-size: 11px; color: #64748b; letter-spacing: 2px; text-transform: uppercase; margin-top: 2px; }
                .content { padding: 24px 32px; }
                .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 20px; }
                .info-item { padding: 10px 14px; background: #f8fafc; border-radius: 8px; border-left: 3px solid #3b82f6; }
                .info-item .label { font-size: 11px; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 2px; }
                .info-item .value { font-size: 15px; font-weight: 600; color: #1e293b; }
                .preferences { margin-bottom: 20px; }
                .preferences h3 { font-size: 13px; color: #64748b; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 10px; border-bottom: 1px solid #e2e8f0; padding-bottom: 6px; }
                .pref-list { display: flex; gap: 12px; }
                .pref-item { flex: 1; padding: 10px; background: #eff6ff; border-radius: 8px; text-align: center; border: 1px solid #bfdbfe; }
                .pref-item .rank { font-size: 11px; color: #3b82f6; font-weight: 600; }
                .pref-item .prog { font-size: 13px; font-weight: 600; color: #1e40af; margin-top: 2px; }
                .instructions { background: #fef3c7; border: 1px solid #fcd34d; border-radius: 8px; padding: 14px 18px; margin-bottom: 20px; }
                .instructions h3 { font-size: 13px; color: #92400e; margin-bottom: 8px; }
                .instructions ul { padding-left: 18px; font-size: 12px; color: #78350f; line-height: 1.8; }
                .footer { display: flex; justify-content: space-between; align-items: center; padding: 16px 32px; border-top: 2px solid #e2e8f0; background: #f8fafc; }
                .footer .signature { text-align: center; }
                .footer .signature .line { width: 160px; border-top: 1px solid #1e293b; margin-top: 40px; padding-top: 4px; font-size: 11px; color: #64748b; }
                .footer .date { font-size: 12px; color: #64748b; }
                @media print { body { background: #fff; padding: 0; } .admit-card { box-shadow: none; border: 2px solid #000; } }
            </style>
            </head>
            <body>
            <div class="admit-card">
                <div class="header">
                    <div class="watermark">ADMIT CARD</div>
                    <h1>SMART UNIVERSITY ADMISSION</h1>
                    <h2>Admit Card - Entrance Examination</h2>
                </div>
                <div class="barcode">
                    ||||| %s |||||
                    <div class="barcode-label">Registration Number</div>
                </div>
                <div class="content">
                    <div class="info-grid">
                        <div class="info-item"><div class="label">Full Name</div><div class="value">%s %s</div></div>
                        <div class="info-item"><div class="label">Registration No</div><div class="value">%s</div></div>
                        <div class="info-item"><div class="label">Email</div><div class="value">%s</div></div>
                        <div class="info-item"><div class="label">Seat Number</div><div class="value">%s</div></div>
                        <div class="info-item"><div class="label">Date of Birth</div><div class="value">%s</div></div>
                        <div class="info-item"><div class="label">Gender</div><div class="value">%s</div></div>
                        <div class="info-item"><div class="label">SSC GPA</div><div class="value">%s</div></div>
                        <div class="info-item"><div class="label">HSC GPA</div><div class="value">%s</div></div>
                    </div>
                    <div class="preferences">
                        <h3>Program Preferences</h3>
                        <div class="pref-list">
                            <div class="pref-item"><div class="rank">1st Preference</div><div class="prog">%s</div></div>
                            <div class="pref-item"><div class="rank">2nd Preference</div><div class="prog">%s</div></div>
                            <div class="pref-item"><div class="rank">3rd Preference</div><div class="prog">%s</div></div>
                        </div>
                    </div>
                    <div class="instructions">
                        <h3>Important Instructions</h3>
                        <ul>
                            <li>Bring this admit card and a valid photo ID to the examination center.</li>
                            <li>Report to the examination center at least 30 minutes before the exam.</li>
                            <li>Electronic devices (mobile phones, calculators) are strictly prohibited.</li>
                            <li>Writing or marking on this admit card will render it invalid.</li>
                            <li>Contact the admission office for any queries.</li>
                        </ul>
                    </div>
                </div>
                <div class="footer">
                    <div class="date">Generated: %s</div>
                    <div class="signature"><div class="line">Authorized Signature & Seal</div></div>
                </div>
            </div>
            </body>
            </html>
            """.formatted(
                reg.getRegistrationNumber(),
                reg.getRegistrationNumber(),
                reg.getFirstName(), reg.getLastName(),
                reg.getRegistrationNumber(),
                reg.getEmail(),
                seatNo,
                reg.getDateOfBirth() != null ? reg.getDateOfBirth().toString() : "N/A",
                reg.getGender() != null ? reg.getGender() : "N/A",
                reg.getSscGpa() != null ? String.valueOf(reg.getSscGpa()) : "N/A",
                reg.getHscGpa() != null ? String.valueOf(reg.getHscGpa()) : "N/A",
                reg.getProgramPreference1() != null ? reg.getProgramPreference1() : "N/A",
                reg.getProgramPreference2() != null ? reg.getProgramPreference2() : "N/A",
                reg.getProgramPreference3() != null ? reg.getProgramPreference3() : "N/A",
                java.time.LocalDate.now().toString()
        );
    }
}
