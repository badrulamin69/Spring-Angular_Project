package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.ProgramSeatConfig;
import com.badrulamin.University_Management.service.ProgramSeatConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/program-seat-configs")
@RequiredArgsConstructor
public class ProgramSeatConfigController {

    private final ProgramSeatConfigService seatConfigService;

    @GetMapping("/config/{configId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<List<ProgramSeatConfig>> getByConfigId(@PathVariable Long configId) {
        return ResponseEntity.ok(seatConfigService.findByConfigId(configId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ProgramSeatConfig> getById(@PathVariable Long id) {
        return ResponseEntity.ok(seatConfigService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ProgramSeatConfig> create(@RequestBody ProgramSeatConfig seatConfig) {
        return ResponseEntity.ok(seatConfigService.create(seatConfig));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ProgramSeatConfig> update(@PathVariable Long id, @RequestBody ProgramSeatConfig seatConfig) {
        return ResponseEntity.ok(seatConfigService.update(id, seatConfig));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        seatConfigService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/config/{configId}/available")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<List<ProgramSeatConfig>> getAvailable(@PathVariable Long configId) {
        return ResponseEntity.ok(seatConfigService.findProgramsWithAvailableSeats(configId));
    }

    @GetMapping("/config/{configId}/summary")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<java.util.Map<String, Object>> getSummary(@PathVariable Long configId) {
        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("totalSeats", seatConfigService.getTotalSeats(configId));
        summary.put("allocatedSeats", seatConfigService.getAllocatedSeats(configId));
        summary.put("remainingSeats", seatConfigService.getTotalSeats(configId) - seatConfigService.getAllocatedSeats(configId));
        return ResponseEntity.ok(summary);
    }
}
