# Plan 003: Add Pagination Size Limits Across All Controllers

**Commit:** `9c70822`
**Category:** Security / Performance
**Impact:** HIGH
**Effort:** S (Small)
**Risk:** Low

---

## Why This Matters

Every paginated endpoint accepts `size` from query params with no upper bound. A request like `GET /api/students?size=999999` would load the entire table into memory, potentially causing OOM or severe DB performance degradation.

**Evidence:**
- `controller/StudentController.java:30` — `@RequestParam(defaultValue = "20") int size`
- `controller/CourseController.java:31` — same pattern
- `controller/PaymentController.java:30` — same pattern
- Every paginated controller (~50+ controllers) has this pattern

---

## Scope

**In scope:**
- All controllers under `controller/` that accept `size` as a `@RequestParam`

**Out of scope:**
- No changes to services or repositories
- No changes to the `PagedResponse` class

---

## Steps

### Step 1: Create a utility class for pagination constants

Create `uni_ms/src/main/java/com/badrulamin/University_Management/config/PaginationConfig.java`:

```java
package com.badrulamin.University_Management.config;

public final class PaginationConfig {
    private PaginationConfig() {}

    public static final int MAX_PAGE_SIZE = 100;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int DEFAULT_PAGE = 0;

    public static int clampSize(int size) {
        return Math.min(Math.max(1, size), MAX_PAGE_SIZE);
    }

    public static int clampPage(int page) {
        return Math.max(0, page);
    }
}
```

### Step 2: Update all paginated controllers

For each controller that accepts `page` and `size` params, add the clamping logic at the top of the method.

**Pattern to follow** (example from `StudentController.java`):

Before:
```java
public ResponseEntity<PagedResponse<Student>> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        ...) {
    Sort sort = ...
    Pageable pageable = PageRequest.of(page, size, sort);
```

After:
```java
public ResponseEntity<PagedResponse<Student>> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        ...) {
    page = PaginationConfig.clampPage(page);
    size = PaginationConfig.clampSize(size);
    Sort sort = ...
    Pageable pageable = PageRequest.of(page, size, sort);
```

**Controllers to update** (all that have paginated endpoints — approximately 50 files):

Group 1 — Academic:
- `AcademicCalendarController`, `AcademicCalendarEventController`, `AcademicPolicyController`, `AcademicResultController`, `AcademicSessionController`
- `CourseController`, `SubjectController`, `CurriculumController`, `BatchController`, `SectionController`
- `FacultyController`, `FacultyAssignmentController`, `TeacherController`, `ClassRoutineController`, `SemesterRoutineController`
- `TimeSlotController`, `PrerequisiteController`, `CreditRuleController`, `GradeRuleController`

Group 2 — Student:
- `StudentController`, `StudentProfileController`, `StudentDocumentController`, `StudentFeeController`
- `GuardianController`, `MedicalInfoController`, `StudentAttendanceController`, `StudentPromotionController`

Group 3 — Admission:
- `AdmissionApplicationController`, `AdmissionCircularController`, `AdmissionCampaignController`
- `AdmissionCandidateController`, `AdmissionTestController`, `AdmissionTestQuestionController`
- `AdmissionMeritListController`, `AdmissionWaitingListController`
- `PreAdmissionRegistrationController`, `ApplicantChoiceController`
- `SeatAllocationController`, `SeatAllocationConfigController`, `ProgramSeatConfigController`

Group 4 — Finance:
- `PaymentController`, `InvoiceController`, `FeeStructureController`, `FeeTypeController`
- `DiscountController`, `FineController`, `TransactionController`

Group 5 — Other:
- `BookController`, `BookCategoryController`, `BookIssueController`, `BookReturnController`
- `HostelController`, `RoomController`, `HostelAllocationController`
- `VehicleController`, `RouteController`, `TransportAllocationController`
- `EmployeeController`, `LeaveRequestController`, `PayrollController`
- `NoticeController`, `MessageController`, `NotificationController`
- `RoleController`, `PermissionController`, `UserController`
- `ExamController`, `ExamScheduleController`, `ExamCenterController`
- `MarkController`, `ResultController`
- `RegistrationController`, `EnrollmentController`, `EnrollmentConfigController`
- `BuildingController`, `ClassroomController`
- `EventController`, `ClubController`, `SportController`

**Total: ~80 controller files.**

For each, add the import and the two clamping lines.

---

## Verification

1. Compile: `cd uni_ms && mvn compile -q`
   Expected: BUILD SUCCESS

2. Search for unclamped `size` params:
   ```bash
   rg -n "RequestParam.*int size" uni_ms/src/main/java/com/badrulamin/University_Management/controller/ | wc -l
   ```
   Should be 0 after all updates.

3. Manual test:
   ```bash
   curl "http://localhost:8085/api/students?size=999999" -H "Authorization: Bearer <token>"
   ```
   Expected: Returns max 20 results (the default), not 999999.

---

## Maintenance Note

- When adding new paginated endpoints, always use `PaginationConfig.clampSize(size)`.
- Consider creating a base controller class that provides a helper method for building `Pageable` from request params.
- The `MAX_PAGE_SIZE` constant (100) can be adjusted if the frontend needs larger pages.

---

## Escape Hatch

If the frontend legitimately needs to export all records (e.g., for a report), add a dedicated `/export` endpoint that returns a file download instead of paginated JSON. Do not increase `MAX_PAGE_SIZE` to accommodate this.
