# Plan 002: Fix FileUploadService Path Traversal Vulnerability

**Commit:** `9c70822`
**Category:** Security
**Impact:** HIGH
**Effort:** S (Small)
**Risk:** Low

---

## Why This Matters

`FileUploadController` accepts a `module` path variable and passes it directly to `FileUploadService.uploadFile()`. The `module` value is used to create a subdirectory under the upload folder. While `Paths.get(...).toAbsolutePath().normalize()` is called, the `subfolder` parameter itself is not validated — a malicious `module` value like `../../etc` could write files outside the upload directory after normalization.

Additionally, `FileUploadService.deleteFile()` and `getFile()` accept a `fileUrl` string that is parsed to a file path. While normalization is applied, there is no check that the resolved path is still within the upload directory.

**Evidence:**
- `uni_ms/src/main/java/com/badrulamin/University_Management/controller/FileUploadController.java:21-22` — `@PathVariable String module` passed directly to service
- `uni_ms/src/main/java/com/badrulamin/University_Management/service/FileUploadService.java:21` — `Path uploadPath = Paths.get(uploadDir, subfolder).toAbsolutePath().normalize();`
- `uni_ms/src/main/java/com/badrulamin/University_Management/service/FileUploadService.java:40` — `Path filePath = Paths.get(uploadDir, relativePath).toAbsolutePath().normalize();`

---

## Scope

**In scope:**
- `uni_ms/src/main/java/com/badrulamin/University_Management/service/FileUploadService.java`
- `uni_ms/src/main/java/com/badrulamin/University_Management/controller/FileUploadController.java`

**Out of scope:**
- No changes to upload directory configuration
- No changes to frontend upload code

---

## Steps

### Step 1: Add path traversal guard to FileUploadService

Edit `uni_ms/src/main/java/com/badrulamin/University_Management/service/FileUploadService.java`:

1. Add a private helper method after the class declaration:
   ```java
   private void assertWithinUploadDir(Path path) throws IOException {
       Path uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
       if (!path.startsWith(uploadRoot)) {
           throw new IOException("Access denied: path resolves outside upload directory");
       }
   }
   ```

2. In `uploadFile()` method, after `Path targetLocation = uploadPath.resolve(filename);` (line 31), add:
   ```java
   assertWithinUploadDir(targetLocation);
   ```

3. In `deleteFile()` method, after `Path filePath = Paths.get(uploadDir, relativePath).toAbsolutePath().normalize();` (line 40), add:
   ```java
   assertWithinUploadDir(filePath);
   ```

4. In `getFile()` method, after `Path filePath = Paths.get(uploadDir, relativePath).toAbsolutePath().normalize();` (line 46), add:
   ```java
   assertWithinUploadDir(filePath);
   ```

### Step 2: Validate module name in controller

Edit `uni_ms/src/main/java/com/badrulamin/University_Management/controller/FileUploadController.java`:

1. Add a validation regex check at the start of `uploadFile()`:
   ```java
   if (module == null || !module.matches("^[a-zA-Z0-9_-]+$")) {
       return ResponseEntity.badRequest().body(Map.of("error", "Invalid module name. Only alphanumeric, hyphens, and underscores are allowed."));
   }
   ```

2. Add file type validation. After the module check:
   ```java
   String originalName = file.getOriginalFilename();
   if (originalName != null) {
       String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase() : "";
       Set<String> allowed = Set.of("jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx", "csv", "txt");
       if (!allowed.contains(ext)) {
           return ResponseEntity.badRequest().body(Map.of("error", "File type ." + ext + " is not allowed."));
       }
   }
   ```

---

## Verification

1. Compile: `cd uni_ms && mvn compile -q`
   Expected: BUILD SUCCESS

2. Manual test — try path traversal:
   ```bash
   curl -X POST http://localhost:8085/api/upload/../../etc -F "file=@test.txt" -H "Authorization: Bearer <token>"
   ```
   Expected: 400 Bad Request with "Invalid module name" error.

3. Manual test — try valid upload:
   ```bash
   curl -X POST http://localhost:8085/api/upload/documents -F "file=@test.pdf" -H "Authorization: Bearer <token>"
   ```
   Expected: 200 OK with URL returned.

---

## Maintenance Note

- If new file types need to be uploaded in the future, add them to the `allowed` set in `FileUploadController`.
- The `assertWithinUploadDir` guard should be maintained if the upload directory configuration changes.
- Consider adding virus scanning for uploaded files in a future iteration.

---

## Escape Hatch

If legitimate modules use dots or special characters in their names, the regex `^[a-zA-Z0-9_-]+$` may be too restrictive. In that case, keep the regex but add specific known module names to an allowlist instead.
