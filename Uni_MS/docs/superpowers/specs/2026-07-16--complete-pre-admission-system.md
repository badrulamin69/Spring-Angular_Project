# Complete Pre-Admission Registration System

## Overview
End-to-end university admission process: Public Registration → Admit Card → Online MCQ Test → Auto-Grading → Merit Processing → Department Allocation → Enrollment as Student.

## User Roles
- **Public**: Register, check status
- **ROLE_APPLICANT**: Login, see dashboard, download admit card, take test, see results, confirm allocation
- **ROLE_ADMISSION_OFFICER**: Manage registrations, create tests/questions, enter marks, process merit, manage allocations

## Complete Flow

### Step 1: Public Registration
- Anyone visits `/pre-admission/register`
- Fills personal info, academic info, program preferences
- System creates PreAdmissionRegistration + User account (ROLE_APPLICANT)
- Returns email + temp password

### Step 2: Admin Review
- Officer sees registration in Pre-Admissions list
- Approve → status becomes ADMIT_CARD_GENERATED
- Admit card HTML generated with barcode, seat number, exam instructions

### Step 3: Applicant Takes Test
- Applicant logs in → sees dashboard with "Take Admission Test" button
- Test has MCQ questions (created by admin)
- Timer-based (e.g. 60 minutes)
- Auto-graded on submit
- Score saved to AdmissionTestAttempt

### Step 4: Merit Processing
- Admin clicks "Process Merit"
- Score = (SSC*10*0.30) + (HSC*10*0.30) + (TestScore*0.40)
- Applicants ranked by score
- DepartmentAllocation created per applicant

### Step 5: Department Allocation
- System matches applicant's preference 1/2/3 to available programs
- Allocation shows: allocated program, department, score, rank
- Applicant confirms or declines allocation

### Step 6: Enrollment
- Confirmed allocation → auto-creates Student entity
- Student gets enrollment number, assigned to batch/section/semester

## New Backend Entities

### AdmissionTestQuestion
- questionText (TEXT)
- optionA, optionB, optionC, optionD (String 500)
- correctOption (String 1: A/B/C/D)
- marks (Double, default 1.0)
- test (ManyToOne → AdmissionTest)

### AdmissionTestAttempt
- registration (ManyToOne → PreAdmissionRegistration)
- test (ManyToOne → AdmissionTest)
- answers (TEXT, JSON: {"questionId": "B", ...})
- totalQuestions (Integer)
- correctAnswers (Integer)
- score (Double)
- maxScore (Double)
- percentage (Double)
- timeTakenSeconds (Integer)
- startedAt (LocalDateTime)
- submittedAt (LocalDateTime)
- status (PENDING/IN_PROGRESS/SUBMITTED/GRADED)

## New Frontend Components

### Applicant Dashboard (`/applicant/dashboard`)
- Status timeline showing all steps
- Current step highlighted
- Actions: Download Admit Card, Take Test, View Results, Confirm Allocation

### Test Taking Component (`/applicant/test/:testId`)
- Question display with 4 options each
- Navigation sidebar (question numbers)
- Countdown timer
- Submit button with confirmation

## API Endpoints

### Applicant Portal (requires ROLE_APPLICANT)
- GET /api/applicant/my-registration — get own registration
- GET /api/applicant/my-test — get available test
- POST /api/applicant/test/{testId}/start — start test attempt
- POST /api/applicant/test/submit — submit answers
- GET /api/applicant/my-results — get test results
- GET /api/applicant/my-allocation — get department allocation
- POST /api/applicant/my-allocation/{id}/confirm — confirm
- POST /api/applicant/my-allocation/{id}/decline — decline

### Admin Test Management (requires ADMISSION_MANAGE)
- CRUD /api/admission-test-questions — question bank
- GET /api/admission-test-questions?testId=X — questions for a test
- GET /api/admission-test-attempts — all attempts
- GET /api/admission-test-attempts?testId=X — attempts per test
