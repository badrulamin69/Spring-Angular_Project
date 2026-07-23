# Plan 008: Unify API Response Envelope Across All Controllers

**Commit:** `9c70822`
**Category:** Architecture
**Impact:** MEDIUM
**Effort:** M (Medium)
**Risk:** Low

---

## Why This Matters

Controllers use two conflicting response patterns:
- Some wrap responses in `ApiResponse<T>` (BuildingController, RegistrationController, AuthController)
- Others return raw entities or `PagedResponse<T>` directly (CourseController, StudentController, PaymentController, etc.)

The `GlobalExceptionHandler` returns a third pattern: `{timestamp, status, error, message}`.

This inconsistency forces the frontend to handle multiple response shapes for errors and data.

**Evidence:**
- `controller/BuildingController.java:41` — `ResponseEntity.ok(ApiResponse.success(pagedResponse))`
- `controller/CourseController.java:41` — `ResponseEntity.ok(response)` (raw PagedResponse)
- `controller/StudentController.java:39` — `ResponseEntity.ok(response)` (raw PagedResponse)
- `controller/PaymentController.java:36` — `ResponseEntity.ok(response)` (raw PagedResponse)
- `exception/GlobalExceptionHandler.java:29` — returns `Map<String, Object>` with different keys

---

## Scope

**In scope:**
- All controllers under `controller/` (~159 files)
- `uni_ms/src/main/java/com/badrulamin/University_Management/exception/GlobalExceptionHandler.java`

**Out of scope:**
- No changes to services
- No changes to the `ApiResponse` class itself (it's already well-designed)
- Frontend changes (frontend must adapt to the unified response)

---

## Steps

### Step 1: Update GlobalExceptionHandler to use ApiResponse

Edit `uni_ms/src/main/java/com/badrulamin/University_Management/exception/GlobalExceptionHandler.java`:

Replace each handler method to return `ApiResponse.error(message)`:

Before (line 23-29):
```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", 404);
    body.put("error", "Not Found");
    body.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
}
```

After:
```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
}
```

Apply the same pattern to ALL handler methods. The `ApiResponse` class already has a `timestamp` field.

For `MethodArgumentNotValidException`, collect field errors into a single message:
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
        String field = ((FieldError) error).getField();
        String message = error.getDefaultMessage();
        errors.put(field, message);
    });
    return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Validation failed", errors));
}
```

### Step 2: Wrap raw entity returns in controllers

For each controller that returns raw entities or `PagedResponse` directly, wrap in `ApiResponse.success()`.

**Pattern A — Simple paginated list:**

Before (e.g., `StudentController.java:27-39`):
```java
public ResponseEntity<PagedResponse<Student>> findAll(...) {
    ...
    return ResponseEntity.ok(response);
}
```

After:
```java
public ResponseEntity<ApiResponse<PagedResponse<Student>>> findAll(...) {
    ...
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

**Pattern B — Single entity:**

Before:
```java
public ResponseEntity<Student> findById(@PathVariable Long id) {
    return ResponseEntity.ok(studentService.findById(id));
}
```

After:
```java
public ResponseEntity<ApiResponse<Student>> findById(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success(studentService.findById(id)));
}
```

**Pattern C — Delete (void):**

Before:
```java
public ResponseEntity<Void> delete(@PathVariable Long id) {
    studentService.delete(id);
    return ResponseEntity.noContent().build();
}
```

After:
```java
public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
    studentService.delete(id);
    return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
}
```

**Controllers already using ApiResponse (no changes needed):**
- `AuthController` — already uses `ApiResponse`
- `BuildingController` — already uses `ApiResponse`
- `RegistrationController` — already uses `ApiResponse`
- `DashboardController` — already uses `ApiResponse`
- `EnrollmentController` — partially (stats endpoint)

**Controllers to update** (all others — approximately 150 files). Priority order:

1. Financial: `PaymentController`, `InvoiceController`, `FeeStructureController`, `FeeTypeController`, `DiscountController`, `FineController`, `TransactionController`
2. Student: `StudentController`, `StudentProfileController`, `StudentDocumentController`, `StudentFeeController`, `GuardianController`
3. Academic: `CourseController`, `SubjectController`, `FacultyController`, `TeacherController`, `ExamController`, `ResultController`, `MarkController`
4. Admission: All admission controllers
5. HR: `EmployeeController`, `LeaveRequestController`, `PayrollController`
6. Infrastructure: `HostelController`, `RoomController`, `BuildingController` (already done), `ClassroomController`
7. Security: `RoleController`, `PermissionController`, `UserController`
8. Library: `BookController`, `BookCategoryController`, `BookIssueController`, `BookReturnController`
9. Transport: `VehicleController`, `RouteController`, `TransportAllocationController`
10. Communication: `NoticeController`, `MessageController`, `NotificationController`

---

## Verification

1. Compile: `cd uni_ms && mvn compile -q`
   Expected: BUILD SUCCESS

2. Grep for un-wrapped returns:
   ```bash
   rg -n "ResponseEntity<[^A]" uni_ms/src/main/java/com/badrulamin/University_Management/controller/ | head -20
   ```
   Expected: No matches (all returns should be `ResponseEntity<ApiResponse<...>>`)

3. Manual test:
   ```bash
   curl http://localhost:8085/api/students -H "Authorization: Bearer <token>"
   ```
   Expected: `{"success":true,"message":"Success","data":{"content":[...],...},"timestamp":"2026-07-24T..."}`
   NOT: `{"content":[...],...}` (raw)

---

## Maintenance Note

- The `RegistrationController.findAll()` (line 92-110) uses a raw `HashMap` response. Update it to use `ApiResponse<PagedResponse<CourseRegistration>>`.
- When adding new controllers, always wrap responses in `ApiResponse.success()`.
- The frontend will need to update its HTTP interceptor/service to unwrap `response.data` instead of `response` directly.
- **Coordinate with frontend team** before deploying this change — it affects every API response.

---

## Escape Hatch

If the frontend cannot be updated immediately, add a configuration property `app.response.envelope=true` and conditionally wrap responses. This allows gradual migration. However, this adds complexity and is not recommended for a single-team project.
