package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.payload.request.ChangePasswordRequest;
import com.badrulamin.University_Management.payload.request.ForgotPasswordRequest;
import com.badrulamin.University_Management.payload.request.LoginRequest;
import com.badrulamin.University_Management.payload.request.RefreshTokenRequest;
import com.badrulamin.University_Management.payload.request.RegisterRequest;
import com.badrulamin.University_Management.payload.request.ResetPasswordRequest;
import com.badrulamin.University_Management.payload.request.SelectRoleRequest;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.AuthService;
import com.badrulamin.University_Management.security.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                               HttpServletRequest request) {
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        try {
            Object result = authService.authenticateUser(loginRequest, ipAddress, userAgent);
            if (result instanceof ApiResponse<?> apiResponse) {
                return ResponseEntity.ok(apiResponse);
            }
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid username or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok((ApiResponse<?>) authService.registerUser(registerRequest));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<ApiResponse<?>> logoutUser(HttpServletRequest request) {
        String jwt = parseJwt(request);
        authService.logoutUser(jwt);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<?>> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return ResponseEntity.ok((ApiResponse<?>) authService.getCurrentUser(userDetails.getUsername()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refreshToken(@Valid @RequestBody RefreshTokenRequest body) {
        return ResponseEntity.ok((ApiResponse<?>) authService.refreshToken(body.getRefreshToken()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok((ApiResponse<?>) authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok((ApiResponse<?>) authService.resetPassword(request));
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                            HttpServletRequest httpRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        String ip = getClientIp(httpRequest);
        String ua = httpRequest.getHeader("User-Agent");
        return ResponseEntity.ok((ApiResponse<?>) authService.changePassword(request, userDetails.getUsername(), ip, ua));
    }

    @GetMapping("/roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> getAllowedRoles() {
        return ResponseEntity.ok((ApiResponse<?>) authService.getAllowedRoles());
    }

    @PostMapping("/select-default-role")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> selectDefaultRole(@Valid @RequestBody SelectRoleRequest body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return ResponseEntity.ok((ApiResponse<?>) authService.selectDefaultRole(body.getRoleCode(), userDetails.getUsername()));
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
