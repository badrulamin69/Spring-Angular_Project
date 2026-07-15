package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.DisciplinaryRecord;
import com.badrulamin.University_Management.service.DisciplinaryRecordService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/disciplinary-records")
public class DisciplinaryRecordController {

    private final DisciplinaryRecordService disciplinaryRecordService;

    public DisciplinaryRecordController(DisciplinaryRecordService disciplinaryRecordService) {
        this.disciplinaryRecordService = disciplinaryRecordService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<DisciplinaryRecord> items = disciplinaryRecordService.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        Map<String, Object> response = new HashMap<>();
        response.put("content", items.getContent());
        response.put("totalElements", items.getTotalElements());
        response.put("totalPages", items.getTotalPages());
        response.put("currentPage", items.getNumber());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<DisciplinaryRecord> findById(@PathVariable Long id) {
        return ResponseEntity.ok(disciplinaryRecordService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<DisciplinaryRecord> create(@RequestBody DisciplinaryRecord disciplinaryRecord) {
        return ResponseEntity.ok(disciplinaryRecordService.create(disciplinaryRecord));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<DisciplinaryRecord> update(@PathVariable Long id, @RequestBody DisciplinaryRecord disciplinaryRecord) {
        return ResponseEntity.ok(disciplinaryRecordService.update(id, disciplinaryRecord));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        disciplinaryRecordService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOpen", disciplinaryRecordService.countByStatus("OPEN"));
        stats.put("totalResolved", disciplinaryRecordService.countByStatus("RESOLVED"));
        stats.put("totalPending", disciplinaryRecordService.countByStatus("PENDING"));
        stats.put("totalDismissed", disciplinaryRecordService.countByStatus("DISMISSED"));
        return ResponseEntity.ok(stats);
    }
}
