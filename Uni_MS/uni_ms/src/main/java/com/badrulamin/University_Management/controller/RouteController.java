package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Route;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.RouteService;
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
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    @PreAuthorize("hasAuthority('TRANSPORT_VIEW')")
    public ResponseEntity<PagedResponse<Route>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Route> paged = routeService.findAll(pageable);
        PagedResponse<Route> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TRANSPORT_VIEW')")
    public ResponseEntity<Route> findById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('TRANSPORT_MANAGE')")
    public ResponseEntity<Route> save(@Valid @RequestBody Route route) {
        return ResponseEntity.ok(routeService.save(route));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TRANSPORT_MANAGE')")
    public ResponseEntity<Route> update(@PathVariable Long id, @Valid @RequestBody Route route) {
        return ResponseEntity.ok(routeService.update(id, route));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TRANSPORT_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
