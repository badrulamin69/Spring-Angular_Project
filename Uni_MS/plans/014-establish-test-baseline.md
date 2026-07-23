# Plan 014: Establish Test Baseline

**Commit:** `9c70822`
**Category:** Testing
**Impact:** HIGH
**Effort:** L (Large)
**Risk:** Low

---

## Why This Matters

The project has zero meaningful tests. The only test file (`UniversityManagementApplicationTests.java`) contains a single `contextLoads()` test that does nothing. This means:
- No regression safety net for any change
- No way to verify business logic correctness
- Refactoring (plans 009, 010, 013) is risky without tests

**Evidence:**
- `src/test/java/.../UniversityManagementApplicationTests.java` — only file, only `contextLoads()`
- No test directory structure beyond the single file
- `pom.xml` includes `spring-boot-starter-test` (test framework is available)

---

## Scope

**In scope:**
- New test files under `src/test/java/com/badrulamin/University_Management/`
- Test configuration (H2 or Testcontainers for test database)

**Out of scope:**
- No changes to production code
- No CI/CD setup (out of scope for this plan)

---

## Steps

### Step 1: Add test database dependency

Edit `uni_ms/pom.xml`, add H2 for testing:
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

### Step 2: Create test application properties

Create `uni_ms/src/test/resources/application.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

jwt.secret=test-secret-key-for-testing-only-32-chars!!
jwt.expiration=86400000
jwt.verification-expiration=3600000
jwt.reset-expiration=900000
app.frontend-url=http://localhost:4200
image.upload.dir=uploads/
```

### Step 3: Create unit tests for critical services

Create test files following JUnit 5 + Mockito pattern.

**`src/test/java/.../service/CourseServiceTest.java`**:
```java
package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Course;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    void findById_existingCourse_returnsCourse() {
        Course course = new Course();
        course.setId(1L);
        course.setName("Test Course");
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course result = courseService.findById(1L);

        assertEquals("Test Course", result.getName());
        verify(courseRepository).findById(1L);
    }

    @Test
    void findById_nonExisting_throwsException() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.findById(999L));
    }

    @Test
    void save_course_returnsSavedCourse() {
        Course course = new Course();
        course.setName("New Course");
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course result = courseService.save(course);

        assertEquals("New Course", result.getName());
        verify(courseRepository).save(course);
    }
}
```

**`src/test/java/.../service/BuildingServiceTest.java`** — follows BuildingService's DTO pattern.

**`src/test/java/.../service/PaymentServiceTest.java`** — tests payment initiation, number generation.

**`src/test/java/.../service/RegistrationServiceTest.java`** — tests course selection, drop, validation.

### Step 4: Create integration tests for critical endpoints

**`src/test/java/.../controller/AuthControllerIntegrationTest.java`**:
```java
package com.badrulamin.University_Management.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"nonexistent\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_missingFields_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
```

### Step 5: Run all tests

```bash
cd uni_ms && mvn test
```

Expected: All tests pass (BUILD SUCCESS).

---

## Verification

1. Compile tests: `cd uni_ms && mvn test-compile -q`
   Expected: BUILD SUCCESS

2. Run tests: `cd uni_ms && mvn test`
   Expected: All tests pass, BUILD SUCCESS

3. Verify test count:
   ```bash
   rg -l "@Test" uni_ms/src/test/ | wc -l
   ```
   Expected: At least 3 test files

---

## Maintenance Note

- Run `mvn test` before every commit to catch regressions.
- New services should have corresponding test classes.
- The H2 database may not support all MySQL-specific SQL (e.g., `GROUP_CONCAT`). If tests fail due to SQL incompatibilities, use Testcontainers with MySQL instead.
- Integration tests that need authentication should use `@WithMockUser` or obtain a JWT token in `@BeforeAll`.

---

## Escape Hatch

If H2 causes compatibility issues with MySQL-specific queries (e.g., native queries in repositories), switch to Testcontainers:
```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mysql</artifactId>
    <version>1.20.0</version>
    <scope>test</scope>
</dependency>
```
And use `@Testcontainers` annotation on integration test classes.
