# Plan 011: Remove Duplicate CORS Configuration

**Commit:** `9c70822`
**Category:** Architecture
**Impact:** LOW
**Effort:** S (Small)
**Risk:** Low

---

## Why This Matters

CORS is configured in TWO places:
1. `WebSecurityConfig.java` (lines 64-72) — `CorsConfigurationSource` bean
2. `WebConfig.java` (lines 27-33) — `addCorsMappings()`

Both allow the same origins. This is redundant and can cause confusion — if one is changed but not the other, CORS behavior becomes unpredictable.

**Evidence:**
- `security/WebSecurityConfig.java:64-72` — `corsConfigurationSource()` bean
- `config/WebConfig.java:27-33` — `addCorsMappings()`

---

## Scope

**In scope:**
- `uni_ms/src/main/java/com/badrulamin/University_Management/config/WebConfig.java`
- `uni_ms/src/main/java/com/badrulamin/University_Management/security/WebSecurityConfig.java`

**Out of scope:**
- No changes to other files

---

## Steps

### Step 1: Remove CORS from WebConfig

Edit `uni_ms/src/main/java/com/badrulamin/University_Management/config/WebConfig.java`:

1. Remove the `addCorsMappings()` override (lines 26-33):
   ```java
   @Override
   public void addCorsMappings(CorsRegistry registry) {
       registry.addMapping("/**")
               .allowedOrigins(frontendUrl, "http://localhost:4200")
               .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
               .allowedHeaders("*")
               .allowCredentials(true)
               .maxAge(3600);
   }
   ```

2. Remove the `@Value("${app.frontend-url}")` field if it's only used by CORS (check — it's also used? If only for CORS, remove it. If also for resource handlers, keep it).

   Looking at `WebConfig.java`, `frontendUrl` is only used in `addCorsMappings`. Remove it.

3. Keep the `FeatureToggleInterceptor` and `addResourceHandlers` methods unchanged.

### Step 2: Verify WebSecurityConfig CORS is sufficient

`WebSecurityConfig.java:64-72` already has:
```java
configuration.setAllowedOrigins(List.of(frontendUrl, "http://localhost:4200"));
configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
configuration.setAllowedHeaders(List.of("*"));
configuration.setAllowCredentials(true);
```

This is sufficient. The `maxAge(3600)` from `WebConfig` is a nice-to-have but not critical — browsers cache CORS preflight responses for 5 minutes by default.

---

## Verification

1. Compile: `cd uni_ms && mvn compile -q`
   Expected: BUILD SUCCESS

2. Verify single CORS config:
   ```bash
   rg -n "addCorsMappings|CorsConfiguration" uni_ms/src/main/java/com/badrulamin/University_Management/config/WebConfig.java
   ```
   Expected: No matches

3. Manual test — CORS preflight:
   ```bash
   curl -X OPTIONS http://localhost:8085/api/students \
     -H "Origin: http://localhost:4200" \
     -H "Access-Control-Request-Method: GET" \
     -v
   ```
   Expected: 200 with `Access-Control-Allow-Origin: http://localhost:4200`

---

## Maintenance Note

- When adding new origins (e.g., production domain), update only `WebSecurityConfig.java`.
- If you need `maxAge` for preflight caching, add it to the `CorsConfiguration` bean in `WebSecurityConfig`:
  ```java
  configuration.setMaxAge(3600L);
  ```

---

## Escape Hatch

If removing CORS from `WebConfig` causes issues with Spring MVC's resource handler CORS (e.g., uploaded files), add `allowedOrigins` to the `addResourceHandlers` method instead of restoring the full `addCorsMappings`.
