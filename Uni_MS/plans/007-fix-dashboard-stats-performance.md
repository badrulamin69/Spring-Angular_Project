# Plan 007: Fix RegistrationService Dashboard Stats Performance

**Commit:** `9c70822`
**Category:** Performance
**Impact:** MEDIUM
**Effort:** S (Small)
**Risk:** Low

---

## Why This Matters

`RegistrationService.getDashboardStats()` loads ALL `CourseRegistration` records for a semester into memory, then uses Java streams to count statuses. On a semester with 5000+ registrations, this loads 5000+ entities into memory just to count them.

**Evidence:**
- `service/RegistrationService.java:235-285` — `getDashboardStats()` calls `courseRegistrationRepository.findBySemester_Id(semesterId)` then does `.stream().filter().count()`

---

## Scope

**In scope:**
- `uni_ms/src/main/java/com/badrulamin/University_Management/service/RegistrationService.java`
- `uni_ms/src/main/java/com/badrulamin/University_Management/repository/CourseRegistrationRepository.java`

**Out of scope:**
- No changes to controllers or other services

---

## Steps

### Step 1: Add count queries to repository

Edit `uni_ms/src/main/java/com/badrulamin/University_Management/repository/CourseRegistrationRepository.java`:

Add these methods:
```java
long countBySemester_Id(Long semesterId);

@Query("SELECT COUNT(cr) FROM CourseRegistration cr WHERE cr.semester.id = :semesterId AND cr.advisorStatus = :status")
long countBySemesterIdAndAdvisorStatus(@Param("semesterId") Long semesterId, @Param("status") String status);

@Query("SELECT COUNT(cr) FROM CourseRegistration cr WHERE cr.semester.id = :semesterId AND cr.status = :status")
long countBySemesterIdAndStatus(@Param("semesterId") Long semesterId, @Param("status") String status);

@Query("SELECT cr.status, COUNT(cr) FROM CourseRegistration cr WHERE cr.semester.id = :semesterId GROUP BY cr.status")
List<Object[]> countGroupByStatus(@Param("semesterId") Long semesterId);
```

### Step 2: Rewrite getDashboardStats()

Edit `uni_ms/src/main/java/com/badrulamin/University_Management/service/RegistrationService.java`:

Replace lines 235-285 with:

```java
public RegistrationDashboardResponse getDashboardStats(Long semesterId) {
    RegistrationDashboardResponse dashboard = new RegistrationDashboardResponse();

    dashboard.setTotalRegistrations(courseRegistrationRepository.countBySemester_Id(semesterId));
    dashboard.setPendingApprovals(courseRegistrationRepository.countBySemesterIdAndAdvisorStatus(semesterId, "PENDING"));
    dashboard.setApprovedRegistrations(courseRegistrationRepository.countBySemesterIdAndAdvisorStatus(semesterId, "APPROVED"));
    dashboard.setRegisteredStudents(courseRegistrationRepository.countBySemesterIdAndStatus(semesterId, "REGISTERED"));
    dashboard.setDroppedRegistrations(courseRegistrationRepository.countBySemesterIdAndStatus(semesterId, "DROPPED"));

    List<Object[]> statusGroups = courseRegistrationRepository.countGroupByStatus(semesterId);
    List<RegistrationDashboardResponse.RegistrationStatsByStatus> breakdown = statusGroups.stream()
            .map(row -> {
                RegistrationDashboardResponse.RegistrationStatsByStatus stat = new RegistrationDashboardResponse.RegistrationStatsByStatus();
                stat.setStatus((String) row[0]);
                stat.setCount((Long) row[1]);
                return stat;
            })
            .collect(Collectors.toList());
    dashboard.setStatusBreakdown(breakdown);

    // For recent registrations, still fetch limited records (this is fine — only 10)
    List<CourseRegistration> recentRecords = courseRegistrationRepository
            .findBySemester_IdOrderByCreatedAtDesc(semesterId, PageRequest.of(0, 10));
    List<RegistrationDashboardResponse.RecentRegistration> recent = recentRecords.stream()
            .map(reg -> {
                RegistrationDashboardResponse.RecentRegistration r = new RegistrationDashboardResponse.RecentRegistration();
                r.setId(reg.getId());
                r.setStudentName(reg.getStudent() != null ? reg.getStudent().getFirstName() + " " + reg.getStudent().getLastName() : null);
                r.setStudentCode(reg.getStudent() != null ? reg.getStudent().getStudentCode() : null);
                r.setCourseName(reg.getCourse() != null ? reg.getCourse().getName() : null);
                r.setSemesterName(reg.getSemester() != null ? reg.getSemester().getName() : null);
                r.setStatus(reg.getStatus());
                r.setCreditHours(reg.getCreditHours());
                r.setRegistrationDate(reg.getCreatedAt());
                return r;
            })
            .collect(Collectors.toList());
    dashboard.setRecentRegistrations(recent);

    return dashboard;
}
```

Also add to `CourseRegistrationRepository`:
```java
List<CourseRegistration> findBySemester_IdOrderByCreatedAtDesc(Long semesterId, Pageable pageable);
```

---

## Verification

1. Compile: `cd uni_ms && mvn compile -q`
   Expected: BUILD SUCCESS

2. Verify no full-table loads for dashboard:
   ```bash
   rg "findBySemester_Id\b" uni_ms/src/main/java/com/badrulamin/University_Management/service/RegistrationService.java
   ```
   Expected: Only the `getStudentRegistrations` method should use the full list. `getDashboardStats` should use count queries.

3. Manual test:
   ```bash
   curl http://localhost:8085/api/registrations/dashboard/1 -H "Authorization: Bearer <token>"
   ```
   Expected: Same response structure but faster execution.

---

## Maintenance Note

- The `countGroupByStatus` query returns `Object[]` which is fragile. If the entity changes, the query must be updated. Consider creating a `StatusCount` projection interface for type safety in a future iteration.
- The `PageRequest.of(0, 10)` for recent registrations is hardcoded. Consider making it configurable.

---

## Escape Hatch

If `findBySemester_IdOrderByCreatedAtDesc` with `Pageable` doesn't work with the repository method name, use a `@Query` annotation instead:
```java
@Query("SELECT cr FROM CourseRegistration cr WHERE cr.semester.id = :semesterId ORDER BY cr.createdAt DESC")
List<CourseRegistration> findRecentBySemester(@Param("semesterId") Long semesterId, Pageable pageable);
```
