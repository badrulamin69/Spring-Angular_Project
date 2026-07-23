# Plan 009: Extract AuthService from AuthController

**Commit:** `9c70822`
**Category:** Architecture
**Impact:** HIGH
**Effort:** M (Medium)
**Risk:** Medium

---

## Why This Matters

`AuthController` is a 661-line god class that handles:
- Login/logout flow with account locking
- Token generation and refresh
- Password reset/change
- Role resolution (duplicated 3 times)
- Login history recording
- Session management
- Activity logging
- Menu tree building

This violates Single Responsibility and makes the auth flow impossible to test or reuse.

**Evidence:**
- `controller/AuthController.java` — 661 lines, 15+ private methods, 8 `@Autowired` repositories
- Lines 144-174, 263-288, 434-439 — role/permission resolution logic duplicated 3 times

---

## Scope

**In scope:**
- `uni_ms/src/main/java/com/badrulamin/University_Management/controller/AuthController.java`
- `uni_ms/src/main/java/com/badrulamin/University_Management/service/AuthService.java` (new)

**Out of scope:**
- No changes to JWT utils, security config, or other controllers
- No changes to entity classes

---

## Steps

### Step 1: Create AuthService

Create `uni_ms/src/main/java/com/badrulamin/University_Management/service/AuthService.java`:

Extract the following from AuthController into AuthService:

```java
package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.*;
import com.badrulamin.University_Management.payload.request.*;
import com.badrulamin.University_Management.payload.response.JwtResponse;
import com.badrulamin.University_Management.repository.*;
import com.badrulamin.University_Management.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final LoginSessionRepository loginSessionRepository;
    private final ActivityLogRepository activityLogRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 30;

    private static final Set<String> ALLOWED_LOGIN_ROLES = Set.of(
        "ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_UNIVERSITY_ADMIN",
        "ROLE_DEPT_HEAD", "ROLE_FACULTY", "ROLE_ADVISOR",
        "ROLE_ADMISSION_OFFICER", "ROLE_ACCOUNTS_OFFICER",
        "ROLE_LIBRARIAN", "ROLE_HALL_PROVOST", "ROLE_TRANSPORT_MANAGER",
        "ROLE_REGISTRAR", "ROLE_HR_MANAGER", "ROLE_FINANCE",
        "ROLE_GENERAL_STAFF", "ROLE_APPLICANT", "ROLE_STUDENT"
    );

    // Move authenticateUser, registerUser, refreshToken, forgotPassword,
    // resetPassword, changePassword, getAllowedRoles, selectDefaultRole
    // methods here from AuthController.

    // Move ALL private helpers here:
    // - resolveRoleName
    // - incrementLoginAttempts
    // - buildMenuTree
    // - menuToMap
    // - recordLoginAttempt
    // - recordLogout
    // - saveRefreshToken
    // - createLoginSession
    // - terminateActiveSession
    // - logActivity
    // - parseBrowser, parseOS, parseDevice
    // - bytesToHex
    // - getClientIp (needs HttpServletRequest — keep this in controller or pass as param)
}
```

### Step 2: Slim down AuthController

The controller should become a thin delegate:

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                               HttpServletRequest request) {
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        return ResponseEntity.ok(authService.authenticateUser(loginRequest, ipAddress, userAgent));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.registerUser(registerRequest));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        String jwt = parseJwt(request);
        authService.logoutUser(jwt);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    // ... etc — thin delegates only
}
```

### Step 3: Eliminate duplicated role resolution

Create a single `resolveUserRoleInfo(User user)` method in `AuthService` that returns a record or map with `roleCode`, `roleName`, `permissions`, `menus`. Use it in `authenticateUser()`, `getCurrentUser()`, and `selectDefaultRole()`.

```java
public record UserRoleInfo(String roleCode, String roleName, List<String> permissions, List<Map<String, Object>> menus) {}

private UserRoleInfo resolveUserRoleInfo(User user) {
    Set<String> roleCodes = new HashSet<>();
    if (user.getRoles() != null) {
        user.getRoles().forEach(r -> roleCodes.add(r.getCode()));
    }
    if (roleCodes.isEmpty() && user.getRole() != null) {
        roleCodes.add(user.getRole().getCode());
    }

    String primaryRoleCode = user.getDefaultRoleCode();
    if (primaryRoleCode == null || primaryRoleCode.isEmpty() || !roleCodes.contains(primaryRoleCode)) {
        primaryRoleCode = roleCodes.isEmpty() ? "ROLE_USER" : roleCodes.iterator().next();
    }

    String roleName = resolveRoleName(user, primaryRoleCode);

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

    List<Map<String, Object>> menus = buildMenuTree(user);

    return new UserRoleInfo(primaryRoleCode, roleName, new ArrayList<>(permissionSet), menus);
}
```

---

## Verification

1. Compile: `cd uni_ms && mvn compile -q`
   Expected: BUILD SUCCESS

2. Verify AuthController is slimmed down:
   ```bash
   wc -l uni_ms/src/main/java/com/badrulamin/University_Management/controller/AuthController.java
   ```
   Expected: < 150 lines (down from 661)

3. Verify AuthService exists and has the logic:
   ```bash
   wc -l uni_ms/src/main/java/com/badrulamin/University_Management/service/AuthService.java
   ```
   Expected: > 300 lines

4. Manual test — login:
   ```bash
   curl -X POST http://localhost:8085/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin123"}'
   ```
   Expected: 200 OK with JWT token

5. Manual test — register:
   ```bash
   curl -X POST http://localhost:8085/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username":"testuser","email":"test@test.com","password":"test123","firstName":"Test","lastName":"User"}'
   ```
   Expected: 200 OK with success message

---

## Maintenance Note

- `getClientIp()` and `parseJwt()` are request-specific utilities. Keep them in the controller as private methods, or extract to a `RequestUtils` utility class.
- The `ALLOWED_LOGIN_ROLES` set should eventually be moved to a configuration class or database table.
- Future work: Add rate limiting to the login endpoint (e.g., 10 attempts per IP per minute).
- This refactoring does NOT change any API contracts — all endpoints remain the same.

---

## Escape Hatch

If the executor finds that extracting AuthService causes circular dependency issues (e.g., AuthService needs SecurityContextHolder which requires the filter chain), keep the `getCurrentUser()` method in the controller and have AuthService handle only the stateful operations (login, register, logout, password operations).
