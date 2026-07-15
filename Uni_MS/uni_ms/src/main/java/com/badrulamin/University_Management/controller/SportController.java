package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Sport;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.SportService;
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
@RequestMapping("/api/sports")
@RequiredArgsConstructor
public class SportController {

    private final SportService sportService;

    @GetMapping
    @PreAuthorize("hasAuthority('ACTIVITY_VIEW')")
    public ResponseEntity<PagedResponse<Sport>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Sport> paged = sportService.findAll(pageable);
        PagedResponse<Sport> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACTIVITY_VIEW')")
    public ResponseEntity<Sport> findById(@PathVariable Long id) {
        return ResponseEntity.ok(sportService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ACTIVITY_MANAGE')")
    public ResponseEntity<Sport> save(@Valid @RequestBody Sport sport) {
        return ResponseEntity.ok(sportService.save(sport));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ACTIVITY_MANAGE')")
    public ResponseEntity<Sport> update(@PathVariable Long id, @Valid @RequestBody Sport sport) {
        return ResponseEntity.ok(sportService.update(id, sport));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ACTIVITY_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
