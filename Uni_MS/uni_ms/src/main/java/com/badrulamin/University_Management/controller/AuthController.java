package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.ActivityLog;
import com.badrulamin.University_Management.entity.LoginHistory;
import com.badrulamin.University_Management.entity.LoginSession;
import com.badrulamin.University_Management.entity.Menu;
import com.badrulamin.University_Management.entity.RefreshToken;
import com.badrulamin.University_Management.entity.Role;
import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.payload.request.ChangePasswordRequest;
import com.badrulamin.University_Management.payload.request.ForgotPasswordRequest;
import com.badrulamin.University_Management.payload.request.LoginRequest;
import com.badrulamin.University_Management.payload.request.RegisterRequest;
import com.badrulamin.University_Management.payload.request.ResetPasswordRequest;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.JwtResponse;
import com.badrulamin.University_Management.repository.ActivityLogRepository;
import com.badrulamin.University_Management.repository.LoginHistoryRepository;
import com.badrulamin.University_Management.repository.LoginSessionRepository;
import com.badrulamin.University_Management.repository.MenuRepository;
import com.badrulamin.University_Management.repository.RefreshTokenRepository;
import com.badrulamin.University_Management.repository.RoleRepository;
import com.badrulamin.University_Management.repository.UserRepository;
import com.badrulamin.University_Management.security.jwt.JwtUtils;
import com.badrulamin.University_Management.security.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 30;

    private static final Set<String> ALLOWED_LOGIN_ROLES = Set.of(
        "ROLE_SUPER_ADMIN",
        "ROLE_ADMIN",
        "ROLE_UNIVERSITY_ADMIN",
        "ROLE_DEPT_HEAD",
        "ROLE_FACULTY",
        "ROLE_ADVISOR",
        "ROLE_ADMISSION_OFFICER",
        "ROLE_ACCOUNTS_OFFICER",
        "ROLE_LIBRARIAN",
        "ROLE_HALL_PROVOST",
        "ROLE_TRANSPORT_MANAGER",
        "ROLE_REGISTRAR",
        "ROLE_HR_MANAGER",
        "ROLE_FINANCE",
        "ROLE_GENERAL_STAFF",
        "ROLE_APPLICANT",
        "ROLE_STUDENT"
    );

    @Autowired AuthenticationManager authenticationManager;
    @Autowired JwtUtils jwtUtils;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired MenuRepository menuRepository;
    @Autowired RefreshTokenRepository refreshTokenRepository;
    @Autowired LoginHistoryRepository loginHistoryRepository;
    @Autowired LoginSessionRepository loginSessionRepository;
    @Autowired ActivityLogRepository activityLogRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                               HttpServletRequest request) {
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        if (user == null) {
            recordLoginAttempt(loginRequest.getUsername(), ipAddress, userAgent, false, "User not found");
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!user.getActive()) {
            recordLoginAttempt(loginRequest.getUsername(), ipAddress, userAgent, false, "Account disabled");
            return ResponseEntity.status(403).body(ApiResponse.error("Your account has been disabled. Please contact administrator."));
        }

        if (user.isLocked()) {
            recordLoginAttempt(loginRequest.getUsername(), ipAddress, userAgent, false, "Account locked");
            return ResponseEntity.status(403).body(ApiResponse.error("Your account is locked. Please try again after " + LOCKOUT_MINUTES + " minutes."));
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            if (user.getRole() == null) {
                recordLoginAttempt(loginRequest.getUsername(), ipAddress, userAgent, false, "No role assigned");
                return ResponseEntity.status(403).body(ApiResponse.error("Your account is not authorized to access this system."));
            }
        }

        boolean hasAllowedRole = false;
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            hasAllowedRole = user.getRoles().stream()
                .anyMatch(r -> ALLOWED_LOGIN_ROLES.contains(r.getCode()));
        }
        if (!hasAllowedRole && user.getRole() != null) {
            hasAllowedRole = ALLOWED_LOGIN_ROLES.contains(user.getRole().getCode());
        }
        if (!hasAllowedRole) {
            recordLoginAttempt(loginRequest.getUsername(), ipAddress, userAgent, false, "Role not authorized");
            return ResponseEntity.status(403).body(ApiResponse.error("Your account is not authorized to access this system."));
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            incrementLoginAttempts(user);
            recordLoginAttempt(loginRequest.getUsername(), ipAddress, userAgent, false, "Invalid credentials");
            throw e;
        } catch (LockedException e) {
            recordLoginAttempt(loginRequest.getUsername(), ipAddress, userAgent, false, "Account locked");
            return ResponseEntity.status(403).body(ApiResponse.error("Your account is locked. Please try again after " + LOCKOUT_MINUTES + " minutes."));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Set<String> roleCodes = new HashSet<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(r -> roleCodes.add(r.getCode()));
        }
        if (roleCodes.isEmpty() && user.getRole() != null) {
            roleCodes.add(user.getRole().getCode());
        }

        String primaryRoleCode = user.getDefaultRoleCode();
        if (primaryRoleCode == null || primaryRoleCode.isEmpty()) {
            primaryRoleCode = roleCodes.isEmpty() ? "ROLE_USER" : roleCodes.iterator().next();
        }
        if (!roleCodes.contains(primaryRoleCode)) {
            primaryRoleCode = roleCodes.isEmpty() ? "ROLE_USER" : roleCodes.iterator().next();
        }

        String finalPrimaryRoleCode = primaryRoleCode;
        String roleName = resolveRoleName(user, finalPrimaryRoleCode);

        Set<String> roleCodeSet = roleCodes;
        Set<String> permissionSet = new HashSet<>();
        if (user.getRoles() != null) {
            for (Role r : user.getRoles()) {
                if (r.getPermissions() != null) {
                    r.getPermissions().forEach(p -> permissionSet.add(p.getCode()));
                }
            }
        }
        if (permissionSet.isEmpty() && user.getRole() != null && user.getRole().getPermissions() != null) {
            user.getRole().getPermissions().forEach(p -> permissionSet.add(p.getCode()));
        }
        List<String> permissions = new ArrayList<>(permissionSet);

        List<Map<String, Object>> menus = buildMenuTree(user);

        recordLoginAttempt(user.getUsername(), ipAddress, userAgent, true, null);

        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(ipAddress);
        user.setLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        saveRefreshToken(refreshToken, user);
        createLoginSession(user, jwt, ipAddress, userAgent);
        logActivity(user.getUsername(), "LOGIN", "Security", "User logged in successfully", "User", String.valueOf(user.getId()), ipAddress, userAgent);

        return ResponseEntity.ok(new JwtResponse(
                jwt, refreshToken, user.getId(), userDetails.getUsername(), userDetails.getEmail(),
                user.getFirstName(), user.getLastName(), user.getAvatar(),
                finalPrimaryRoleCode, roleName, new ArrayList<>(roleCodes), permissions, menus));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Username is already taken"));
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email is already in use"));
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPhone(registerRequest.getPhone());
        user.setActive(true);
        user.setEmailVerified(false);

        String roleCode = registerRequest.getRoleCode() != null ? registerRequest.getRoleCode() : "ROLE_STUDENT";
        Optional<Role> roleOpt = roleRepository.findByCode(roleCode);
        if (roleOpt.isPresent()) {
            Role role = roleOpt.get();
            if (!ALLOWED_LOGIN_ROLES.contains(role.getCode())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Registration with this role is not permitted"));
            }
            user.setRole(role);
            user.addRole(role);
        }

        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully. Please verify your email.", null));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        String jwt = parseJwt(request);
        if (jwt != null) {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                refreshTokenRepository.deleteByUser_Id(user.getId());
                terminateActiveSession(user.getId());
                String ip = getClientIp(request);
                String ua = request.getHeader("User-Agent");
                logActivity(username, "LOGOUT", "Security", "User logged out", "User", String.valueOf(user.getId()), ip, ua);
                recordLogout(user);
            }
        }
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        Set<String> roleCodes = new HashSet<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(r -> roleCodes.add(r.getCode()));
        }
        if (roleCodes.isEmpty() && user.getRole() != null) {
            roleCodes.add(user.getRole().getCode());
        }

        String primaryRoleCode = user.getDefaultRoleCode();
        if (primaryRoleCode == null || primaryRoleCode.isEmpty()) {
            primaryRoleCode = roleCodes.isEmpty() ? "ROLE_USER" : roleCodes.iterator().next();
        }
        String roleName = resolveRoleName(user, primaryRoleCode);

        Set<String> permissionSet2 = new HashSet<>();
        if (user.getRoles() != null) {
            for (Role r : user.getRoles()) {
                if (r.getPermissions() != null) {
                    r.getPermissions().forEach(p -> permissionSet2.add(p.getCode()));
                }
            }
        }
        if (permissionSet2.isEmpty() && user.getRole() != null && user.getRole().getPermissions() != null) {
            user.getRole().getPermissions().forEach(p -> permissionSet2.add(p.getCode()));
        }
        List<String> permissions = new ArrayList<>(permissionSet2);

        List<Map<String, Object>> menus = buildMenuTree(user);

        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("firstName", user.getFirstName());
        profile.put("lastName", user.getLastName());
        profile.put("phone", user.getPhone());
        profile.put("avatar", user.getAvatar());
        profile.put("roleCode", primaryRoleCode);
        profile.put("roleName", roleName);
        profile.put("roles", new ArrayList<>(roleCodes));
        profile.put("permissions", permissions);
        profile.put("menus", menus);

        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> body) {
        String refreshTokenStr = body.get("refreshToken");
        if (refreshTokenStr == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Refresh token required"));
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr).orElse(null);
        if (refreshToken == null || refreshToken.getRevoked()) {
            return ResponseEntity.status(403).body(ApiResponse.error("Invalid refresh token"));
        }
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Refresh token expired"));
        }

        User user = refreshToken.getUser();
        if (!user.getActive()) {
            return ResponseEntity.status(403).body(ApiResponse.error("Account is disabled"));
        }
        if (user.isLocked()) {
            return ResponseEntity.status(403).body(ApiResponse.error("Account is locked"));
        }

        String newAccessToken = jwtUtils.generateTokenForUser(user);
        String newRefreshToken = jwtUtils.generateRefreshTokenForUser(user);

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        saveRefreshToken(newRefreshToken, user);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("token", newAccessToken);
        response.put("refreshToken", newRefreshToken);
        response.put("type", "Bearer");

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user != null) {
            String resetToken = UUID.randomUUID().toString();
            user.setPasswordResetToken(resetToken);
            user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(user);
        }
        return ResponseEntity.ok(ApiResponse.success("If the email exists, a password reset link has been sent.", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        User user = userRepository.findByPasswordResetToken(request.getToken()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid or expired reset token"));
        }
        if (user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Reset token has expired"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        refreshTokenRepository.deleteByUser_Id(user.getId());

        return ResponseEntity.ok(ApiResponse.success("Password reset successfully. Please login with your new password.", null));
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                            HttpServletRequest httpRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Current password is incorrect"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);

        refreshTokenRepository.deleteByUser_Id(user.getId());

        String ip = getClientIp(httpRequest);
        String ua = httpRequest.getHeader("User-Agent");
        logActivity(user.getUsername(), "PASSWORD_CHANGE", "Security", "Password changed successfully", "User", String.valueOf(user.getId()), ip, ua);

        return ResponseEntity.ok(ApiResponse.success("Password changed successfully. Please login again.", null));
    }

    @GetMapping("/roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllowedRoles() {
        List<Map<String, String>> roles = new ArrayList<>();
        for (String roleCode : ALLOWED_LOGIN_ROLES) {
            roleRepository.findByCode(roleCode).ifPresent(role -> {
                Map<String, String> r = new LinkedHashMap<>();
                r.put("code", role.getCode());
                r.put("name", role.getName());
                roles.add(r);
            });
        }
        return ResponseEntity.ok(ApiResponse.success(roles));
    }

    @PostMapping("/select-default-role")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> selectDefaultRole(@RequestBody Map<String, String> body) {
        String roleCode = body.get("roleCode");
        if (roleCode == null || !ALLOWED_LOGIN_ROLES.contains(roleCode)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid role"));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        boolean hasRole = false;
        if (user.getRoles() != null) {
            hasRole = user.getRoles().stream().anyMatch(r -> r.getCode().equals(roleCode));
        }
        if (!hasRole && user.getRole() != null) {
            hasRole = user.getRole().getCode().equals(roleCode);
        }
        if (!hasRole) {
            return ResponseEntity.status(403).body(ApiResponse.error("You do not have this role"));
        }

        user.setDefaultRoleCode(roleCode);
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success("Default role updated", null));
    }

    // ===== Helpers =====

    private String resolveRoleName(User user, String roleCode) {
        if (user.getRoles() != null) {
            for (Role r : user.getRoles()) {
                if (r.getCode().equals(roleCode)) return r.getName();
            }
        }
        if (user.getRole() != null && user.getRole().getCode().equals(roleCode)) {
            return user.getRole().getName();
        }
        return "";
    }

    private void incrementLoginAttempts(User user) {
        try {
            int attempts = user.getLoginAttempts() != null ? user.getLoginAttempts() : 0;
            attempts++;
            user.setLoginAttempts(attempts);
            if (attempts >= MAX_LOGIN_ATTEMPTS) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES));
            }
            userRepository.save(user);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(AuthController.class).error("Failed to increment login attempts for user: {}", user.getUsername(), e);
        }
    }

    private List<Map<String, Object>> buildMenuTree(User user) {
        Set<String> permCodeSet = new HashSet<>();
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                if (role.getPermissions() != null) {
                    role.getPermissions().forEach(p -> permCodeSet.add(p.getCode()));
                }
            }
        }
        if (permCodeSet.isEmpty() && user.getRole() != null && user.getRole().getPermissions() != null) {
            user.getRole().getPermissions().forEach(p -> permCodeSet.add(p.getCode()));
        }
        List<String> permCodes = new ArrayList<>(permCodeSet);
        List<Menu> menus = menuRepository.findAuthorizedMenus(permCodes);
        return menus.stream().map(menu -> menuToMap(menu, permCodes)).collect(Collectors.toList());
    }

    private Map<String, Object> menuToMap(Menu menu, List<String> userPermissions) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", menu.getId());
        map.put("title", menu.getTitle());
        map.put("icon", menu.getIcon());
        map.put("route", menu.getRoute());
        map.put("orderNo", menu.getOrderNo());
        map.put("permissionCode", menu.getPermissionCode());
        map.put("module", menu.getModule());
        map.put("visible", menu.getVisible());
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            List<Map<String, Object>> filteredChildren = menu.getChildren().stream()
                    .filter(child -> (child.getActive() == null || child.getActive()) 
                            && (child.getVisible() == null || child.getVisible())
                            && (child.getPermissionCode() == null || userPermissions.contains(child.getPermissionCode())))
                    .map(child -> menuToMap(child, userPermissions))
                    .collect(Collectors.toList());
            if (!filteredChildren.isEmpty()) {
                map.put("children", filteredChildren);
            }
        }
        return map;
    }

    private void recordLoginAttempt(String username, String ip, String ua, boolean success, String reason) {
        try {
            LoginHistory lh = new LoginHistory();
            lh.setUsername(username);
            lh.setIpAddress(ip);
            lh.setUserAgent(ua);
            lh.setLoginTimestamp(LocalDateTime.now());
            lh.setSuccess(success);
            lh.setFailureReason(reason);
            User user = userRepository.findByUsername(username).orElse(null);
            lh.setUser(user);
            loginHistoryRepository.save(lh);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(AuthController.class).error("Failed to record login attempt for user: {}", username, e);
        }
    }

    private void recordLogout(User user) {
        try {
            loginHistoryRepository.findTopByUser_IdAndLogoutTimestampIsNullOrderByLoginTimestampDesc(user.getId())
                .ifPresent(h -> {
                    h.setLogoutTimestamp(LocalDateTime.now());
                    loginHistoryRepository.save(h);
                });
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(AuthController.class).error("Failed to record logout for user: {}", user.getUsername(), e);
        }
    }

    private void saveRefreshToken(String token, User user) {
        RefreshToken rt = new RefreshToken();
        rt.setToken(token);
        rt.setUser(user);
        rt.setExpiryDate(LocalDateTime.now().plusDays(7));
        rt.setRevoked(false);
        refreshTokenRepository.save(rt);
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

    private void createLoginSession(User user, String token, String ipAddress, String userAgent) {
        try {
            LoginSession session = new LoginSession();
            session.setUser(user);
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            String tokenHash = bytesToHex(hash);
            session.setSessionToken(tokenHash);
            session.setIpAddress(ipAddress);
            session.setLoginTime(LocalDateTime.now());
            session.setActive(true);
            if (userAgent != null) {
                session.setBrowser(parseBrowser(userAgent));
                session.setOperatingSystem(parseOS(userAgent));
                session.setDeviceType(parseDevice(userAgent));
            }
            loginSessionRepository.save(session);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(AuthController.class).error("Failed to create login session for user: {}", user.getUsername(), e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private void terminateActiveSession(Long userId) {
        try {
            List<LoginSession> sessions = loginSessionRepository.findByIsActiveTrueAndUser_Id(userId);
            for (LoginSession session : sessions) {
                session.setActive(false);
                session.setLogoutTime(LocalDateTime.now());
                loginSessionRepository.save(session);
            }
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(AuthController.class).error("Failed to terminate active sessions for user id: {}", userId, e);
        }
    }

    private void logActivity(String username, String action, String module, String description, String entityType, String entityId, String ipAddress, String userAgent) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUsername(username);
            log.setAction(action);
            log.setModule(module);
            log.setDescription(description);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setIpAddress(ipAddress);
            log.setUserAgent(userAgent);
            User user = userRepository.findByUsername(username).orElse(null);
            log.setUser(user);
            activityLogRepository.save(log);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(AuthController.class).error("Failed to log activity for user: {} action: {}", username, action, e);
        }
    }

    private String parseBrowser(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Edg")) return "Edge";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari")) return "Safari";
        return "Other";
    }

    private String parseOS(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac")) return "macOS";
        if (userAgent.contains("Linux")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iOS") || userAgent.contains("iPhone")) return "iOS";
        return "Other";
    }

    private String parseDevice(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Mobile") || userAgent.contains("Android") || userAgent.contains("iPhone")) return "Mobile";
        if (userAgent.contains("Tablet") || userAgent.contains("iPad")) return "Tablet";
        return "Desktop";
    }
}
