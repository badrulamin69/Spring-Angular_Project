# Plan 005: Add @Transactional Annotations Consistently Across Services

**Commit:** `9c70822`
**Category:** Correctness
**Impact:** HIGH
**Effort:** S (Small)
**Risk:** Low

---

## Why This Matters

Most services lack `@Transactional` annotations. When a service method performs multiple repository calls (e.g., save entity A, then save entity B), a failure between them leaves the database in an inconsistent state. Only `PaymentService` (class-level) and `EnrollmentService` / `RegistrationService` (method-level) have proper transactional boundaries.

**Evidence:**
- `service/CourseService.java` — no `@Transactional` anywhere
- `service/StudentService.java` — no `@Transactional` anywhere
- `service/ExamService.java` — no `@Transactional` anywhere
- `service/FacultyService.java` — no `@Transactional` anywhere
- `service/BuildingService.java` — no `@Transactional` anywhere
- `service/BookService.java` — no `@Transactional` anywhere (presumed same pattern)

---

## Scope

**In scope:**
- All service files under `service/` (~143 files)

**Out of scope:**
- No changes to controllers, entities, or repositories
- No changes to services that already have correct `@Transactional` annotations

---

## Steps

### Step 1: Identify services needing @Transactional

Services fall into three categories:

**A. Read-only services (query only):** Add `@Transactional(readOnly = true)` at class level.
These are services that only call `findAll()`, `findById()`, `search*()`, `count()`.

Examples: `CourseService`, `StudentService`, `ExamService`, `FacultyService`, `SubjectService`, `BatchService`, `SectionService`, `DepartmentService`, `ProgramService`, `SemesterService`, `BookService`, `HostelService`, `RoomService`, `VehicleService`, `RouteService`

Pattern for these — add at class level:
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {
```

**B. Write services (create/update/delete):** Add `@Transactional` at class level.
These are services that call `save()`, `delete()`, `update()`.

Examples: `BuildingService`, `FeeTypeService`, `NoticeService`, `AnnouncementService`, `ClubService`, `SportService`, `EventService`

Pattern:
```java
@Service
@RequiredArgsConstructor
@Transactional
public class BuildingService {
```

**C. Mixed services (reads + writes):** Add `@Transactional(readOnly = true)` at class level, then `@Transactional` on individual write methods.

Examples: `PaymentService` (already has class-level `@Transactional`), `RegistrationService` (has method-level), `EnrollmentService` (has method-level)

For these, verify existing annotations are correct and add `readOnly = true` to read methods if class-level is `@Transactional`.

### Step 2: Apply to all services

For each service file, add the appropriate annotation:

1. Add import: `import org.springframework.transaction.annotation.Transactional;`
2. Add annotation to class (or verify existing one is correct).

**Do NOT change existing `@Transactional` on methods in `RegistrationService` or `EnrollmentService`** — they are correct.

### Step 3: Handle special cases

- `AuthController` helper methods that call repositories: These belong in a service (plan 009), but for now, add `@org.springframework.transaction.annotation.Transactional` to the `recordLoginAttempt`, `saveRefreshToken`, `createLoginSession`, `logActivity` private methods. (Already partially done — `logoutUser` has it on line 233.)

---

## Verification

1. Compile: `cd uni_ms && mvn compile -q`
   Expected: BUILD SUCCESS

2. Verify annotation presence:
   ```bash
   rg -l "@Transactional" uni_ms/src/main/java/com/badrulamin/University_Management/service/ | wc -l
   ```
   Expected: Should be close to 143 (all services).

3. Verify no service is missing:
   ```bash
   for f in uni_ms/src/main/java/com/badrulamin/University_Management/service/*.java; do
     grep -qL "@Transactional" "$f" && echo "MISSING: $f"
   done
   ```
   Expected: No output (all files have `@Transactional`).

---

## Maintenance Note

- When creating new services, always add the appropriate `@Transactional` annotation.
- `@Transactional(readOnly = true)` enables Hibernate flush mode optimization and prevents accidental writes in read methods.
- Class-level `@Transactional` can be overridden at method level — this is the correct pattern for mixed services.
- Be careful with `@Transactional` on `@RestController` methods — it works but is discouraged (prefer service-level).

---

## Escape Hatch

If a service is truly stateless (only delegates to a single repository call with no business logic), `@Transactional` is technically unnecessary but still recommended for consistency and future-proofing.
