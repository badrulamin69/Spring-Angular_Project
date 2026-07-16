# Pre-Admission Registration Pipeline

## Overview

Implement a complete student pre-admission registration flow: Public Registration → Admit Card → Admission Test → Merit Processing → Department Allocation → Enrollment.

## Scope

- **IN:** Pre-admission registration, admit card generation, multi-component test marks, merit processing, automatic department allocation
- **OUT:** Notifications (email/SMS), payment gateway integration, reports/analytics (future phases)

## Flow

```
Public Register → Officer Approves → Admit Card Generated → Test Marks Entered → Merit Processed → Department Auto-Allocated → Applicant Confirms → Enrollment Created
```

## Status State Machine

```
DRAFT → SUBMITTED → ADMIT_CARD_GENERATED → TEST_COMPLETED → MERIT_PROCESSED → ALLOCATED → ENROLLED
                  ↘ REJECTED
```

---

## Backend

### Entity 1: PreAdmissionRegistration

**Table:** `pre_admission_registration`
**Extends:** BaseEntity

| Field | Type | Constraints |
|---|---|---|
| registrationNumber | String | NOT NULL, unique |
| firstName | String | NOT NULL, max 100 |
| lastName | String | NOT NULL, max 100 |
| email | String | NOT NULL, unique |
| phone | String | max 20 |
| dateOfBirth | LocalDate | NOT NULL |
| gender | String | max 20 |
| address | String | TEXT |
| photoUrl | String | max 500 |
| fatherName | String | max 100 |
| motherName | String | max 100 |
| guardianPhone | String | max 20 |
| sscGpa | Double | |
| sscYear | Integer | |
| sscBoard | String | max 100 |
| hscGpa | Double | |
| hscYear | Integer | |
| hscBoard | String | max 100 |
| programPreference1 | String | NOT NULL |
| programPreference2 | String | |
| programPreference3 | String | |
| status | String | NOT NULL, default DRAFT |
| remarks | String | max 2000 |
| isEmailVerified | Boolean | default false |

**Relationships:**
- ManyToOne → AcademicSession (session)
- ManyToOne → AdmissionCircular (circular)

### Entity 2: AdmissionTestResult

**Table:** `admission_test_result`
**Extends:** BaseEntity

| Field | Type | Constraints |
|---|---|---|
| writtenMarks | Double | |
| mcqMarks | Double | |
| vivaMarks | Double | |
| writtenMax | Double | default 100 |
| mcqMax | Double | default 100 |
| vivaMax | Double | default 50 |
| totalWeightedScore | Double | |
| status | String | PENDING/SCORED/REVIEWED |
| remarks | String | max 1000 |

**Relationships:**
- ManyToOne → PreAdmissionRegistration (registration)
- ManyToOne → AdmissionTest (test)

### Entity 3: DepartmentAllocation

**Table:** `department_allocation`
**Extends:** BaseEntity

| Field | Type | Constraints |
|---|---|---|
| allocationNumber | String | NOT NULL, unique |
| meritRank | Integer | |
| totalScore | Double | |
| status | String | PENDING/ALLOCATED/CONFIRMED/CANCELLED |
| allocatedAt | LocalDateTime | |
| confirmedAt | LocalDateTime | |
| remarks | String | max 2000 |

**Relationships:**
- ManyToOne → PreAdmissionRegistration (registration)
- ManyToOne → Program (allocatedProgram)
- ManyToOne → Department (allocatedDepartment)
- ManyToOne → Batch (allocatedBatch)
- ManyToOne → Section (allocatedSection)
- ManyToOne → Semester (semester)
- ManyToOne → User (allocatedBy)

---

### API Endpoints

#### Public (no auth):
- POST `/api/pre-admission/register` — Submit registration
- GET `/api/pre-admission/status/{registrationNumber}` — Check status

#### Officer (ADMISSION_MANAGE):
- GET `/api/pre-admissions` — List (paginated, filterable)
- GET `/api/pre-admissions/{id}` — Detail
- PUT `/api/pre-admissions/{id}/approve` — Approve + generate admit card
- PUT `/api/pre-admissions/{id}/reject` — Reject
- GET `/api/pre-admissions/{id}/admit-card` — Download admit card PDF

#### Test Marks:
- POST `/api/admission-test-results` — Enter marks
- PUT `/api/admission-test-results/{id}` — Update marks
- GET `/api/admission-test-results` — List all

#### Merit + Allocation:
- POST `/api/pre-admissions/process-merit/{sessionId}` — Process merit
- GET `/api/pre-admissions/merit-list/{sessionId}` — View merit list
- GET `/api/department-allocations` — List allocations
- PUT `/api/department-allocations/{id}/confirm` — Confirm
- PUT `/api/department-allocations/{id}/cancel` — Cancel

---

## Frontend

### Pages:
1. `/pre-admission/register` — Public multi-step registration form
2. `/pre-admission/status` — Public status check
3. `/admissions/pre-admissions` — Officer: registration list
4. `/admissions/pre-admissions/:id` — Officer: registration detail + admit card
5. `/admissions/test-results` — Officer: marks entry
6. `/admissions/merit-processing` — Officer: process merit
7. `/admissions/allocations` — Officer: allocation list

### Services: PreAdmissionService, AdmissionTestResultService, DepartmentAllocationService
### Models: PreAdmissionRegistration, AdmissionTestResult, DepartmentAllocation

---

## Roles & Permissions

- **Public:** Register, check status
- **Admission Officer:** View all, approve/reject, generate admit card, enter marks, process merit, manage allocations
- **Applicant:** View own registration, download admit card, view result

## Database Changes

- 3 new tables only
- No modifications to existing tables
- New permissions: PRE_ADMISSION_VIEW, PRE_ADMISSION_MANAGE
- New menu items under Admissions
