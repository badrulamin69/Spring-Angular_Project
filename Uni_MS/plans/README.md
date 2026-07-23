# Uni_MS Backend Improvement Plans

**Generated against commit:** `9c70822`
**Date:** 2026-07-24
**Base path:** `uni_ms/src/main/java/com/badrulamin/University_Management/`

---

## Findings Summary

| # | Finding | Category | Impact | Effort | Risk | Confidence |
|---|---------|----------|--------|--------|------|------------|
| 1 | Hardcoded credentials in application.properties | Security | CRITICAL | S | Low | HIGH |
| 2 | FileUploadService path traversal via unsanitized `subfolder` | Security | HIGH | S | Low | HIGH |
| 3 | No pagination size limit — DoS via `size=999999` | Security | HIGH | S | Low | HIGH |
| 4 | Raw JPA entities returned in ~80% of controllers | Security/Architecture | CRITICAL | L | Medium | HIGH |
| 5 | Inconsistent API response envelope (some `ApiResponse`, some raw) | Architecture | MEDIUM | M | Low | HIGH |
| 6 | AuthController is a 661-line god class | Architecture | HIGH | M | Medium | HIGH |
| 7 | No `@Transactional` on most services | Correctness | HIGH | S | Low | HIGH |
| 8 | `PaymentService.generatePaymentNumber()` loads ALL payments into memory | Performance | HIGH | S | Low | HIGH |
| 9 | `RegistrationService.getDashboardStats()` loads all records to count | Performance | MEDIUM | S | Low | HIGH |
| 10 | `update()` in services overwrites entire entity from request body | Correctness | MEDIUM | M | Medium | HIGH |
| 11 | Duplicate CORS config in both `WebConfig` and `WebSecurityConfig` | Architecture | LOW | S | Low | HIGH |
| 12 | No input validation on `PaymentController` POST endpoints | Security | MEDIUM | S | Low | HIGH |
| 13 | Zero test coverage (only `contextLoads()` test exists) | Testing | HIGH | L | Low | HIGH |
| 14 | `BaseEntity` soft delete not enforced in repository queries | Correctness | MEDIUM | M | Low | HIGH |
| 15 | Sequential Long IDs exposed in API paths | Security | MEDIUM | L | High | MEDIUM |
| 16 | No service interfaces — all concrete classes | Architecture | MEDIUM | L | Low | MEDIUM |

---

## Recommended Execution Order

Plans are ordered by dependency and leverage. Security plans first, then correctness, then architecture.

| Order | Plan | Depends On | Status |
|-------|------|------------|--------|
| 1 | `001-remove-hardcoded-credentials.md` | — | DONE |
| 2 | `002-fix-file-upload-path-traversal.md` | — | DONE |
| 3 | `003-add-pagination-size-limits.md` | — | DONE |
| 4 | `004-add-payment-input-validation.md` | — | DONE |
| 5 | `005-add-service-transactions.md` | — | DONE |
| 6 | `006-fix-payment-number-generation.md` | — | DONE |
| 7 | `007-fix-dashboard-stats-performance.md` | — | DONE |
| 8 | `008-unify-api-response-envelope.md` | — | DONE |
| 9 | `009-extract-auth-service.md` | 008 | DONE |
| 10 | `010-fix-service-update-pattern.md` | — | DONE |
| 11 | `011-remove-duplicate-cors.md` | — | DONE |
| 12 | `012-enforce-soft-delete.md` | — | DONE |
| 13 | `013-add-entity-response-dtos.md` | 008 | DONE |
| 14 | `014-establish-test-baseline.md` | — | DONE |

**Not planned (deferred):**
- Sequential Long → UUID IDs (plan 015): High risk of breaking all frontend API calls. Requires coordinated frontend migration. Recommend as a separate major version effort.
- Service interfaces (plan 016): Low immediate value for this project size. Concrete injection via `@RequiredArgsConstructor` works fine. Revisit if codebase grows past 200 services.

---

## Implementation Summary (2026-07-24)

All 14 plans executed successfully. Build: `BUILD SUCCESS`. Tests: **34/34 passing**.

### Files Created (new)
- `config/PaginationConfig.java` — pagination size limits
- `config/EntityUpdateUtil.java` — generic entity merge utility
- `service/AuthService.java` — extracted from AuthController (588 lines)
- `payload/request/PaymentInitiateRequest.java` — validated payment input DTO
- `payload/request/PaymentRefundRequest.java` — validated refund input DTO
- `payload/request/StudentRequest.java`, `CourseRequest.java`, `ExamRequest.java`, `EmployeeRequest.java`, `RoleRequest.java`
- `payload/response/UserResponse.java`, `StudentResponse.java`, `PaymentResponse.java`, `CourseResponse.java`, `ExamResponse.java`, `EmployeeResponse.java`, `InvoiceResponse.java`, `RoleResponse.java`
- `.env.example` — environment variable documentation
- `src/test/resources/application.properties` — H2 test config
- 5 test files (unit + integration tests, 33 tests)

### Files Modified (existing)
- `application.properties` — removed hardcoded credentials
- `DatabaseSeeder.java` — passwords from env vars
- `FileUploadService.java` — path traversal guard
- `FileUploadController.java` — module/file validation
- `WebConfig.java` — removed duplicate CORS
- `BaseEntity.java` — added `@Where` for soft delete
- `PaymentRepository.java` / `RefundRepository.java` — optimized queries
- `PaymentService.java` — optimized number generation, added toResponse()
- `CourseRegistrationRepository.java` — count queries for dashboard
- `RegistrationService.java` — optimized dashboard stats
- `CourseService.java`, `StudentService.java`, `ExamService.java`, `EmployeeService.java`, `FacultyService.java`, `BookService.java`, `HostelService.java`, `RoomService.java`, `VehicleService.java`, `RouteService.java`, `DepartmentService.java`, `ProgramService.java`, `SemesterService.java`, `BatchService.java`, `SectionService.java`, `NoticeService.java`, `AnnouncementService.java`, `ClubService.java`, `SportService.java` — added @Transactional, fixed update pattern, added toResponse()
- `AuthController.java` — slimmed from 661 to ~127 lines
- **132+ controllers** — pagination clamping
- **143+ controllers** — ApiResponse wrapping
- `GlobalExceptionHandler.java` — returns ApiResponse
- `pom.xml` — added H2 test dependency

---

## Notes

- CI/CD pipeline still does not exist. Build verification uses `mvnw.cmd compile` and `mvnw.cmd test` locally.
- Frontend must adapt to new unified `ApiResponse` envelope (`response.data` instead of `response`).
- Frontend must update `PaymentController` calls from query params to JSON body.
- Deferred: UUID IDs (breaking frontend), Service interfaces (low value at current scale).
