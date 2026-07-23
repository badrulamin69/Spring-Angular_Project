# Plan 010: Fix Service Update Pattern (Prevent Entity Overwrite)

**Commit:** `9c70822`
**Category:** Correctness
**Impact:** MEDIUM
**Effort:** M (Medium)
**Risk:** Medium

---

## Why This Matters

Services like `CourseService.update()`, `StudentService.update()`, and `ExamService.update()` call `findById()` to check existence, then overwrite the entire entity with the request body data. This means:
- Fields not in the request (e.g., `createdAt`, `createdBy`, `version`) are reset
- The `@Version` field is overwritten, breaking optimistic locking
- If the frontend sends partial data, other fields are silently lost

**Evidence:**
- `service/CourseService.java:36-40`:
  ```java
  public Course update(Long id, Course course) {
      findById(id);
      course.setId(id);
      return courseRepository.save(course);
  }
  ```
- `service/StudentService.java:47-50` — identical pattern
- `service/ExamService.java:46-49` — identical pattern
- `service/FacultyService.java:34-37` — identical pattern

---

## Scope

**In scope:**
- All services with `update()` methods that follow this pattern (~30+ services)

**Out of scope:**
- No changes to controllers
- No changes to entities
- No changes to services that already have proper update logic (e.g., `BuildingService`)

---

## Steps

### Step 1: Create a generic merge utility

Create `uni_ms/src/main/java/com/badrulamin/University_Management/config/EntityUpdateUtil.java`:

```java
package com.badrulamin.University_Management.config;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.util.Set;

@Component
public class EntityUpdateUtil {

    /**
     * Copies non-null, non-ID, non-audit properties from source to target.
     * Preserves: id, version, createdAt, updatedAt, createdBy, updatedBy, deleted, deletedAt, deletedBy
     */
    public void merge(Object source, Object target) {
        Set<String> skipProps = Set.of(
            "id", "version", "createdAt", "updatedAt",
            "createdBy", "updatedBy", "deleted", "deletedAt", "deletedBy"
        );

        PropertyDescriptor[] targetPds = java.beans.Introspector.getBeanInfo(target.getClass()).getPropertyDescriptors();
        PropertyAccessor targetAccessor = PropertyAccessorFactory.forBeanPropertyAccess(target);

        for (PropertyDescriptor pd : targetPds) {
            if (skipProps.contains(pd.getName())) continue;
            if (pd.getReadMethod() == null || pd.getWriteMethod() == null) continue;

            try {
                PropertyAccessor sourceAccessor = PropertyAccessorFactory.forBeanPropertyAccess(source);
                if (!sourceAccessor.isReadableProperty(pd.getName())) continue;

                Object value = sourceAccessor.getPropertyValue(pd.getName());
                if (value != null) {
                    targetAccessor.setPropertyValue(pd.getName(), value);
                }
            } catch (Exception ignored) {
            }
        }
    }
}
```

### Step 2: Update services to use merge

For each service with the broken `update()` pattern, inject `EntityUpdateUtil` and change `update()`:

**Example — CourseService:**

Before:
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {
    private final CourseRepository courseRepository;

    public Course update(Long id, Course course) {
        findById(id);
        course.setId(id);
        return courseRepository.save(course);
    }
}
```

After:
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {
    private final CourseRepository courseRepository;
    private final EntityUpdateUtil entityUpdateUtil;

    @Transactional
    public Course update(Long id, Course incoming) {
        Course existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return courseRepository.save(existing);
    }
}
```

Apply this pattern to all services with the same issue:
- `CourseService`, `StudentService`, `ExamService`, `FacultyService`
- `BookService`, `HostelService`, `RoomService`, `VehicleService`, `RouteService`
- `DepartmentService`, `ProgramService`, `SemesterService`, `BatchService`, `SectionService`
- `FeeTypeService`, `NoticeService`, `AnnouncementService`, `ClubService`, `SportService`
- `EmployeeService`, `TeacherService`, and any other service with `update(Long id, Entity e)` pattern

**Do NOT change** `BuildingService` — it already has proper field-by-field update logic.

---

## Verification

1. Compile: `cd uni_ms && mvn compile -q`
   Expected: BUILD SUCCESS

2. Verify no services still overwrite:
   ```bash
   rg -n "\.setId\(id\)" uni_ms/src/main/java/com/badrulamin/University_Management/service/
   ```
   Expected: Zero matches (the old pattern used `course.setId(id)`).

3. Manual test — update a course:
   ```bash
   curl -X PUT http://localhost:8085/api/courses/1 \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <token>" \
     -d '{"name":"Updated Course","code":"CS101","durationYears":3}'
   ```
   Expected: Course is updated but `createdAt`, `createdBy`, `version` fields are preserved.

---

## Maintenance Note

- The `EntityUpdateUtil.merge()` method skips null values — this is intentional (partial updates). If full replacement is needed for some fields, create a separate `replace()` method.
- If entities have nested objects (e.g., `Course.department`), `merge()` won't deep-merge them. For nested objects, the frontend should send the full nested object or just the ID.
- This approach works with Lombok `@Getter/@Setter` entities. If some entities use custom setters, they may need adjustment.

---

## Escape Hatch

If `EntityUpdateUtil` causes issues with specific entities (e.g., custom property types, collections), fall back to manual field-by-field update for those specific services. The generic utility covers ~80% of cases.
