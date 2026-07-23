# Plan 013: Add Entity Response DTOs for High-Risk Controllers

**Commit:** `9c70822`
**Category:** Security / Architecture
**Impact:** CRITICAL
**Effort:** L (Large)
**Risk:** Medium

---

## Why This Matters

~80% of controllers return raw JPA entities directly. This exposes:
- Internal database fields (`version`, `createdAt`, `createdBy`, `deleted`, `deletedAt`)
- Password reset tokens (`passwordResetToken`, `passwordResetTokenExpiry` on `User`)
- Nested object graphs (e.g., `Payment` → `Invoice` → `Student` → `User` → `Role` → `Permission`)
- Potential circular reference stack overflows

This plan covers the highest-risk entities: `User`, `Student`, `Payment`, `Employee`, `Invoice`, `Course`, `Exam`, `Role`.

**Evidence:**
- `entity/User.java` — `passwordResetToken` (line 66), `emailVerificationToken` (line 64) exposed on serialization
- `entity/Payment.java` — `gatewayResponse` (line 54) exposed
- `controller/StudentController.java:50` — `@RequestBody Student student` accepts full entity as input
- `controller/CourseController.java:52` — `@RequestBody Course course` accepts full entity as input

---

## Scope

**In scope:**
- New Response DTOs for: `User`, `Student`, `Payment`, `Employee`, `Invoice`, `Course`, `Exam`, `Role`
- New Request DTOs for: `Student`, `Course`, `Exam`, `Employee`, `Role` (POST/PUT)
- Controller updates for the 8 controllers listed above

**Out of scope:**
- All other entities (can be added incrementally in future plans)
- Service layer changes (services continue returning entities; controllers map to DTOs)

---

## Steps

### Step 1: Create Response DTOs

Create response DTOs in `payload/response/` following the `BuildingResponse` pattern:

**`UserResponse.java`** (excludes sensitive fields):
```java
package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String avatar;
    private Boolean active;
    private Boolean emailVerified;
    private String roleCode;
    private String roleName;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}
```

**`StudentResponse.java`**:
```java
@Data
public class StudentResponse {
    private Long id;
    private String studentCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
    private LocalDate enrollmentDate;
    private String status;
    private Long departmentId;
    private String departmentName;
    private LocalDateTime createdAt;
}
```

**`PaymentResponse.java`** (excludes gateway internals):
```java
@Data
public class PaymentResponse {
    private Long id;
    private String paymentNumber;
    private Long invoiceId;
    private Long studentId;
    private String studentName;
    private Double amount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paymentDate;
    private String notes;
    private LocalDateTime createdAt;
}
```

Follow the same pattern for: `CourseResponse`, `ExamResponse`, `EmployeeResponse`, `InvoiceResponse`, `RoleResponse`.

### Step 2: Create Request DTOs

**`StudentRequest.java`**:
```java
@Data
public class StudentRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @Email private String email;
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
    @NotNull private Long departmentId;
}
```

Follow the same pattern for: `CourseRequest`, `ExamRequest`, `EmployeeRequest`, `RoleRequest`.

### Step 3: Add mapping methods to services

For each entity, add a `toResponse()` method in the service:

**Example — `StudentService`**:
```java
private StudentResponse toResponse(Student student) {
    StudentResponse response = new StudentResponse();
    response.setId(student.getId());
    response.setStudentCode(student.getStudentCode());
    response.setFirstName(student.getFirstName());
    response.setLastName(student.getLastName());
    response.setEmail(student.getEmail());
    response.setPhone(student.getPhone());
    response.setGender(student.getGender());
    response.setDateOfBirth(student.getDateOfBirth());
    response.setEnrollmentDate(student.getEnrollmentDate());
    response.setStatus(student.getStatus());
    response.setDepartmentId(student.getDepartment() != null ? student.getDepartment().getId() : null);
    response.setDepartmentName(student.getDepartment() != null ? student.getDepartment().getName() : null);
    response.setCreatedAt(student.getCreatedAt());
    return response;
}
```

### Step 4: Update controllers

Change return types from raw entities to DTOs:

**Example — `StudentController`**:
```java
@GetMapping
@PreAuthorize("hasAuthority('STUDENT_VIEW')")
public ResponseEntity<ApiResponse<PagedResponse<StudentResponse>>> findAll(...) {
    ...
    Page<Student> paged = studentService.searchStudents(...);
    Page<StudentResponse> dtoPage = paged.map(studentService::toResponse);
    PagedResponse<StudentResponse> response = new PagedResponse<>(...);
    return ResponseEntity.ok(ApiResponse.success(response));
}

@PostMapping
@PreAuthorize("hasAuthority('STUDENT_CREATE')")
public ResponseEntity<ApiResponse<StudentResponse>> save(@Valid @RequestBody StudentRequest request) {
    return ResponseEntity.ok(ApiResponse.success(studentService.save(request)));
}
```

---

## Verification

1. Compile: `cd uni_ms && mvn compile -q`
   Expected: BUILD SUCCESS

2. Verify no password tokens in User responses:
   ```bash
   curl http://localhost:8085/api/users -H "Authorization: Bearer <token>"
   ```
   Expected: No `passwordResetToken`, `emailVerificationToken`, `password` fields in response.

3. Verify Student response has clean shape:
   ```bash
   curl http://localhost:8085/api/students -H "Authorization: Bearer <token>"
   ```
   Expected: Fields match `StudentResponse` DTO (no `version`, `deleted`, `createdBy`).

4. Verify input validation on StudentRequest:
   ```bash
   curl -X POST http://localhost:8085/api/students \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <token>" \
     -d '{"firstName":""}'
   ```
   Expected: 400 with validation error "First name is required"

---

## Maintenance Note

- When adding new fields to entities, remember to add them to the corresponding Response DTO.
- The `BuildingController`/`BuildingService` already follows this pattern — use it as the reference implementation.
- This is the most impactful plan but also the largest. Execute it after plans 008 (response envelope) to avoid doing the wrapping twice.
- Consider using MapStruct or ModelMapper for DTO mapping if manual mapping becomes tedious.

---

## Escape Hatch

If mapping all 8 entities at once is too much, start with just `User` and `Student` (highest security risk) and defer the rest. The `UserResponse` alone eliminates the password token exposure vulnerability.
