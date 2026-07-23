package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Vehicle;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.VehicleService;
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
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    @PreAuthorize("hasAuthority('TRANSPORT_VIEW')")
    public ResponseEntity<ApiResponse<PagedResponse<Vehicle>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Vehicle> paged = vehicleService.findAll(pageable);
        PagedResponse<Vehicle> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TRANSPORT_VIEW')")
    public ResponseEntity<ApiResponse<Vehicle>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(vehicleService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('TRANSPORT_MANAGE')")
    public ResponseEntity<ApiResponse<Vehicle>> save(@Valid @RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(ApiResponse.success(vehicleService.save(vehicle)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TRANSPORT_MANAGE')")
    public ResponseEntity<ApiResponse<Vehicle>> update(@PathVariable Long id, @Valid @RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(ApiResponse.success(vehicleService.update(id, vehicle)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TRANSPORT_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
