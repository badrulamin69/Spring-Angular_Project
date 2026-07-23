# Plan 006: Fix PaymentService Payment Number Generation Performance

**Commit:** `9c70822`
**Category:** Performance
**Impact:** HIGH
**Effort:** S (Small)
**Risk:** Low

---

## Why This Matters

`PaymentService.generatePaymentNumber()` loads ALL payments from the database into memory just to find the maximum sequence number. On a system with 100K+ payments, this is an O(n) full table scan that happens on every single payment initiation.

**Evidence:**
- `service/PaymentService.java:186-205` — `generatePaymentNumber()` calls `paymentRepository.findAll()`
- `service/PaymentService.java:207-227` — `generateRefundNumber()` calls `refundRepository.findAll()`

---

## Scope

**In scope:**
- `uni_ms/src/main/java/com/badrulamin/University_Management/service/PaymentService.java`
- `uni_ms/src/main/java/com/badrulamin/University_Management/repository/PaymentRepository.java`
- `uni_ms/src/main/java/com/badrulamin/University_Management/repository/RefundRepository.java`

**Out of scope:**
- No changes to other services or controllers

---

## Steps

### Step 1: Add query methods to repositories

Edit `uni_ms/src/main/java/com/badrululamin/University_Management/repository/PaymentRepository.java`:

Add these methods:
```java
@Query("SELECT COALESCE(MAX(CAST(SUBSTRING(p.paymentNumber, :prefixLength + 1) AS long)), 0) FROM Payment p WHERE p.paymentNumber LIKE CONCAT(:prefix, '%')")
Long findMaxSequenceByPrefix(@Param("prefix") String prefix, @Param("prefixLength") int prefixLength);
```

Edit `uni_ms/src/main/java/com/badrulamin/University_Management/repository/RefundRepository.java`:

Add this method:
```java
@Query("SELECT COALESCE(MAX(CAST(SUBSTRING(r.refundNumber, :prefixLength + 1) AS long)), 0) FROM Refund r WHERE r.refundNumber LIKE CONCAT(:prefix, '%')")
Long findMaxSequenceByPrefix(@Param("prefix") String prefix, @Param("prefixLength") int prefixLength);
```

### Step 2: Update PaymentService

Edit `uni_ms/src/main/java/com/badrulamin/University_Management/service/PaymentService.java`:

Replace `generatePaymentNumber()` (lines 186-205):

Before:
```java
private String generatePaymentNumber() {
    String prefix = "PAY-" + Year.now().getValue() + "-";
    List<Payment> allPayments = paymentRepository.findAll();

    AtomicLong maxSeq = new AtomicLong(0);
    allPayments.forEach(p -> {
        if (p.getPaymentNumber() != null && p.getPaymentNumber().startsWith(prefix)) {
            try {
                String seqPart = p.getPaymentNumber().substring(prefix.length());
                long seq = Long.parseLong(seqPart);
                if (seq > maxSeq.get()) {
                    maxSeq.set(seq);
                }
            } catch (NumberFormatException ignored) {
            }
        }
    });

    long nextSeq = maxSeq.get() + 1;
    return prefix + String.format("%06d", nextSeq);
}
```

After:
```java
private String generatePaymentNumber() {
    String prefix = "PAY-" + Year.now().getValue() + "-";
    Long maxSeq = paymentRepository.findMaxSequenceByPrefix(prefix, prefix.length());
    long nextSeq = (maxSeq != null ? maxSeq : 0L) + 1;
    return prefix + String.format("%06d", nextSeq);
}
```

Replace `generateRefundNumber()` (lines 207-227) with the same pattern using `refundRepository.findMaxSequenceByPrefix()`.

Remove unused imports: `java.util.concurrent.atomic.AtomicLong`, `java.util.List` (if no longer needed).

---

## Verification

1. Compile: `cd uni_ms && mvn compile -q`
   Expected: BUILD SUCCESS

2. Verify no `findAll()` calls in PaymentService for number generation:
   ```bash
   rg "findAll" uni_ms/src/main/java/com/badrulamin/University_Management/service/PaymentService.java
   ```
   Expected: Only the `findAll(Pageable pageable)` method remains (the paginated one). No unpaginated `findAll()`.

3. Manual test — create a payment and verify the number format:
   ```bash
   curl -X POST http://localhost:8085/api/payments/initiate \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <token>" \
     -d '{"invoiceId":1,"studentId":1,"amount":100,"paymentMethod":"CASH"}'
   ```
   Expected: Response contains `paymentNumber` like `"PAY-2026-000001"`.

---

## Maintenance Note

- The `CAST(... AS long)` JPQL may need adjustment depending on the JPA provider. If Hibernate doesn't support `CAST` to long, use a native query instead:
  ```java
  @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(payment_number, :prefixLength + 1) AS UNSIGNED)), 0) FROM payments WHERE payment_number LIKE CONCAT(:prefix, '%')", nativeQuery = true)
  ```
- If the application runs on multiple instances concurrently, the sequence generation could have race conditions. Consider using a database sequence or `SELECT ... FOR UPDATE` for production deployments.

---

## Escape Hatch

If the JPQL `CAST` syntax doesn't work with the current Hibernate version, fall back to a native SQL query. The repository method signature stays the same; only the `@Query` annotation changes.
