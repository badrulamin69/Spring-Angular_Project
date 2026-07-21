package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.payload.request.RegistrationConfigRequest;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.RegistrationConfigResponse;
import com.badrulamin.University_Management.service.RegistrationConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registration-configs")
@RequiredArgsConstructor
public class RegistrationConfigController {

    private final RegistrationConfigService registrationConfigService;

    @GetMapping
    @PreAuthorize("hasAuthority('REGISTRATION_VIEW')")
    public ResponseEntity<ApiResponse<List<RegistrationConfigResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(registrationConfigService.findAll()));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('REGISTRATION_VIEW')")
    public ResponseEntity<ApiResponse<List<RegistrationConfigResponse>>> findActive() {
        return ResponseEntity.ok(ApiResponse.success(registrationConfigService.findActive()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('REGISTRATION_VIEW')")
    public ResponseEntity<ApiResponse<RegistrationConfigResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(registrationConfigService.findById(id)));
    }

    @GetMapping("/semester/{semesterId}")
    @PreAuthorize("hasAuthority('REGISTRATION_VIEW')")
    public ResponseEntity<ApiResponse<RegistrationConfigResponse>> findBySemester(@PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(registrationConfigService.findBySemester(semesterId)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('REGISTRATION_MANAGE')")
    public ResponseEntity<ApiResponse<RegistrationConfigResponse>> create(@Valid @RequestBody RegistrationConfigRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Registration config created successfully", registrationConfigService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('REGISTRATION_MANAGE')")
    public ResponseEntity<ApiResponse<RegistrationConfigResponse>> update(@PathVariable Long id, @Valid @RequestBody RegistrationConfigRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Registration config updated successfully", registrationConfigService.update(id, request)));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAuthority('REGISTRATION_MANAGE')")
    public ResponseEntity<ApiResponse<RegistrationConfigResponse>> closeRegistration(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Registration closed successfully", registrationConfigService.closeRegistration(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('REGISTRATION_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        registrationConfigService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Registration config deleted successfully", null));
    }
}
