package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.LoginSession;
import com.badrulamin.University_Management.service.LoginSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/login-sessions")
@RequiredArgsConstructor
public class LoginSessionController {

    private final LoginSessionService loginSessionService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<Page<LoginSession>> findAll(Pageable pageable) {
        return ResponseEntity.ok(loginSessionService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<LoginSession> findById(@PathVariable Long id) {
        return ResponseEntity.ok(loginSessionService.findById(id));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<List<LoginSession>> findActiveSessions() {
        return ResponseEntity.ok(loginSessionService.findActiveSessions());
    }

    @GetMapping("/active/count")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<Long> getActiveSessionCount() {
        return ResponseEntity.ok(loginSessionService.getActiveSessionCount());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<List<LoginSession>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(loginSessionService.findByUserId(userId));
    }

    @GetMapping("/user/{userId}/active")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<List<LoginSession>> findActiveSessionsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(loginSessionService.findActiveSessionsByUserId(userId));
    }

    @PostMapping("/{id}/terminate")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<LoginSession> terminateSession(@PathVariable Long id) {
        return ResponseEntity.ok(loginSessionService.terminateSession(id));
    }

    @PostMapping("/user/{userId}/terminate-all")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<Void> terminateAllUserSessions(@PathVariable Long userId) {
        loginSessionService.terminateAllUserSessions(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        loginSessionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
