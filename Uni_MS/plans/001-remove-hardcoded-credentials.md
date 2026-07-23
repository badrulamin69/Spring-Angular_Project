# Plan 001: Remove Hardcoded Credentials from application.properties

**Commit:** `9c70822`
**Category:** Security
**Impact:** CRITICAL
**Effort:** S (Small)
**Risk:** Low

---

## Why This Matters

`application.properties` contains hardcoded fallback values for database password, mail password, JWT secret, and Spring Security credentials. These are committed to git, meaning anyone with repo access has production/default credentials. The mail password (`MAIL_PASSWORD` fallback) is a Gmail app-specific password. The JWT secret fallback is used when the `JWT_SECRET` env var is absent.

**Evidence:**
- `uni_ms/src/main/resources/application.properties:17` — `spring.datasource.password=${DB_PASSWORD:1234}`
- `uni_ms/src/main/resources/application.properties:39` — `spring.mail.password=${MAIL_PASSWORD:xuhj hxdc njvn fmct}`
- `uni_ms/src/main/resources/application.properties:51-52` — `spring.security.user.name=admin` / `spring.security.user.password=1234`
- `uni_ms/src/main/resources/application.properties:54` — `jwt.secret=${JWT_SECRET:7f3k9mX2pQrL8nVwYdA5sEuJhBcGiTemran6lKvF1yWqPmHbCxDjZ0eUsIgOt}`
- `uni_ms/src/main/java/com/badrulamin/University_Management/config/DatabaseSeeder.java:374` — hardcoded demo passwords: `"admin123"`, `"registrar123"`, `"hr123"`

---

## Scope

**In scope:**
- `uni_ms/src/main/resources/application.properties`
- `uni_ms/src/main/java/com/badrulamin/University_Management/config/DatabaseSeeder.java`

**Out of scope:**
- No changes to Java service/controller code
- No changes to `application.properties` structure (just credential values)

---

## Steps

### Step 1: Replace hardcoded credential fallbacks with mandatory env vars

Edit `uni_ms/src/main/resources/application.properties`:

1. **Line 17** — Change DB password fallback to empty string:
   ```
   spring.datasource.password=${DB_USERNAME:root}
   ```
   becomes:
   ```
   spring.datasource.password=${DB_PASSWORD:}
   ```
   This forces the env var to be set or the app fails to start (intentional — prevents running with no DB auth).

2. **Line 39** — Remove the mail password fallback entirely:
   ```
   spring.mail.password=${MAIL_PASSWORD:}
   ```
   The app should not start without mail config if email features are used. If email is optional, keep the empty fallback but remove the real app password.

3. **Lines 51-52** — Remove or comment out the Spring Security default user entirely:
   ```properties
   # spring.security.user.name=admin
   # spring.security.user.password=1234
   ```
   These are auto-config defaults that conflict with the JWT-based auth system.

4. **Line 54** — Remove the JWT secret fallback value:
   ```
   jwt.secret=${JWT_SECRET:}
   ```
   The app should fail to start if `JWT_SECRET` is not set.

### Step 2: Move demo passwords out of source code

Edit `uni_ms/src/main/java/com/badrulamin/University_Management/config/DatabaseSeeder.java`:

1. **Line 374** — Change `createUser("admin", ..., "admin123", ...)` to read password from env:
   ```java
   String adminPassword = System.getenv().getOrDefault("SEED_ADMIN_PASSWORD", UUID.randomUUID().toString().substring(0, 12));
   createUser("admin", "admin@erp.com", adminPassword, "System", "Administrator", "1234567890", superAdmin);
   ```

2. **Lines 377-378** — Same for `registrar` and `hrmanager` users.

### Step 3: Add a `.env.example` file

Create `uni_ms/.env.example` (not `.env`) documenting required environment variables:
```env
# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=academymanagement
DB_USERNAME=root
DB_PASSWORD=

# JWT
JWT_SECRET=

# Mail
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=
MAIL_PASSWORD=

# Seed
SEED_ADMIN_PASSWORD=
```

---

## Verification

1. Run: `cd uni_ms && mvn compile -q`
   Expected: BUILD SUCCESS (no compile errors from the changes)

2. Grep for hardcoded credentials:
   ```bash
   rg -n "1234" uni_ms/src/main/resources/application.properties
   rg -n "admin123|registrar123|hr123" uni_ms/src/main/java/com/badrulamin/University_Management/config/DatabaseSeeder.java
   rg -n "xuhj" uni_ms/src/main/resources/application.properties
   rg -n "7f3k9m" uni_ms/src/main/resources/application.properties
   ```
   Expected: All return zero matches.

3. Verify `.env.example` exists:
   ```bash
   test -f uni_ms/.env.example && echo "OK"
   ```
   Expected: `OK`

---

## Maintenance Note

- After this change, any deployment **must** set `JWT_SECRET`, `DB_PASSWORD`, and `MAIL_PASSWORD` environment variables before starting the app.
- Add `uni_ms/.env` (not `.env.example`) to `.gitignore` if not already present.
- The old JWT secret value should be considered compromised and rotated in any deployed environment.
- **Rotate the Gmail app-specific password** (`MAIL_PASSWORD`) if this repo was ever public or shared.

---

## Escape Hatch

If the application refuses to start because env vars are missing during local development, create a `uni_ms/.env` file locally (never commit it) with development-only values. The app can be configured to load `.env` via `spring.config.import=optional:file:.env` or via IDE run configuration.
