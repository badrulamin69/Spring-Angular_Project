# Plan 012: Enforce Soft Delete Filtering in Repository Queries

**Commit:** `9c70822`
**Category:** Correctness
**Impact:** MEDIUM
**Effort:** M (Medium)
**Risk:** Low

---

## Why This Matters

`BaseEntity` defines a soft delete mechanism (`deleted` flag, `deletedAt`, `deletedBy`), and `@PreRemove` sets `deleted = true`. However, no repository queries filter by `is_deleted = false`. This means `findAll()`, `findById()`, and custom queries all return soft-deleted records, making soft delete effectively useless.

**Evidence:**
- `entity/BaseEntity.java:49-50` — `@Column(name = "is_deleted", nullable = false) private Boolean deleted = false;`
- `entity/BaseEntity.java:52-56` — `@PreRemove` sets deleted flag
- No `@Where` annotation on any entity
- No `@Filter` annotation on any entity manager

---

## Scope

**In scope:**
- `uni_ms/src/main/java/com/badrulamin/University_Management/entity/BaseEntity.java`
- All entity classes that extend `BaseEntity` (~140 entities)

**Out of scope:**
- No changes to controllers or services
- No changes to repository interfaces
- No changes to queries

---

## Steps

### Step 1: Add @Where annotation to BaseEntity

Edit `uni_ms/src/main/java/com/badrulamin/University_Management/entity/BaseEntity.java`:

1. Add import: `import org.hibernate.annotations.Where;`

2. Add `@Where` annotation to the class:
   ```java
   @Getter
   @Setter
   @MappedSuperclass
   @EntityListeners(AuditingEntityListener.class)
   @Where(clause = "is_deleted = false")
   public abstract class BaseEntity {
   ```

This automatically adds `WHERE is_deleted = false` to ALL Hibernate queries for any entity extending `BaseEntity`.

### Step 2: Verify Hibernate version compatibility

`@Where` is a Hibernate-specific annotation. The project uses Spring Boot 3.4.1 which ships Hibernate 6.6+. `@Where` is supported in Hibernate 6.

If the project switches to a non-Hibernate JPA provider, this won't work. But since `application.properties` uses `spring.jpa.hibernate.ddl-auto=update`, Hibernate is confirmed as the provider.

### Step 3: Handle edge cases

Some code may need to access soft-deleted records (e.g., admin audit views). For those specific queries, add `@Where(clause = "1=1")` to override, or use a native query.

In `RegistrationService.getDashboardStats()`, if deleted registrations should be excluded (they should), the `@Where` annotation handles this automatically.

---

## Verification

1. Compile: `cd uni_ms && mvn compile -q`
   Expected: BUILD SUCCESS

2. Verify @Where is on BaseEntity:
   ```bash
   rg "@Where" uni_ms/src/main/java/com/badrulamin/University_Management/entity/BaseEntity.java
   ```
   Expected: `@Where(clause = "is_deleted = false")`

3. Manual test — soft delete a course:
   ```bash
   curl -X DELETE http://localhost:8085/api/courses/1 -H "Authorization: Bearer <token>"
   curl http://localhost:8085/api/courses -H "Authorization: Bearer <token>"
   ```
   Expected: Course 1 is NOT in the list (soft-deleted).

4. Verify deleted record still exists in DB:
   ```sql
   SELECT id, is_deleted FROM courses WHERE id = 1;
   ```
   Expected: `is_deleted = 1`

---

## Maintenance Note

- `@Where` applies to ALL queries by default. If you need to query soft-deleted records (e.g., for admin recovery), use a `@Query` with `is_deleted = true` or `@Where(clause = "1=1")` on the specific repository method.
- `@Where` does NOT apply to `@OneToMany`/`@ManyToMany` collections unless those entities also extend `BaseEntity`. Check that collections eagerly load non-deleted records.
- If bulk deletes are needed (permanent), use `DELETE FROM entity WHERE id = :id` (native query) to bypass the filter.

---

## Escape Hatch

If `@Where` causes issues with specific queries (e.g., joins that need deleted records), use Spring Data JPA's `@Query` annotation with explicit `WHERE is_deleted = false` on those specific methods instead. The `@Where` annotation is a blanket approach; individual queries can override it.
