# Plan 004: Add Input Validation to PaymentController POST Endpoints

**Commit:** `9c70822`
**Category:** Security / Correctness
**Impact:** MEDIUM
**Effort:** S (Small)
**Risk:** Low

---

## Why This Matters

`PaymentController.initiatePayment()` accepts 5 `@RequestParam` values instead of a `@RequestBody`. This means:
- No Bean Validation (`@NotNull`, `@Positive`, etc.) is applied
- Negative payment amounts are accepted
- Arbitrary `paymentMethod` strings are accepted (should be an enum)
- `refundPayment()` has no check that refund amount doesn't exceed payment amount

**Evidence:**
- `controller/PaymentController.java:91-98` — `initiatePayment` with 5 `@RequestParam`
- `controller/PaymentController.java:131-137` — `refundPayment` with no amount validation
- `service/PaymentService.java:61-81` — `initiatePayment` creates payment with unvalidated amount

---

## Scope

**In scope:**
- `uni_ms/src/main/java/com/badrulamin/University_Management/controller/PaymentController.java`
- `uni_ms/src/main/java/com/badrulamin/University_Management/payload/request/PaymentInitiateRequest.java` (new)
- `uni_ms/src/main/java/com/badrulamin/University_Management/payload/request/PaymentRefundRequest.java` (new)

**Out of scope:**
- No changes to `PaymentService` business logic
- No changes to other controllers

---

## Steps

### Step 1: Create request DTOs

Create `uni_ms/src/main/java/com/badrulamin/University_Management/payload/request/PaymentInitiateRequest.java`:

```java
package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PaymentInitiateRequest {
    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "^(CASH|BANK_TRANSFER|CARD|MOBILE_BANKING|ONLINE)$",
             message = "Payment method must be one of: CASH, BANK_TRANSFER, CARD, MOBILE_BANKING, ONLINE")
    private String paymentMethod;

    private String notes;
}
```

Create `uni_ms/src/main/java/com/badrulamin/University_Management/payload/request/PaymentRefundRequest.java`:

```java
package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PaymentRefundRequest {
    @NotNull(message = "Refund amount is required")
    @Positive(message = "Refund amount must be positive")
    private Double amount;

    @NotBlank(message = "Reason is required")
    private String reason;

    private String approvedBy;
}
```

### Step 2: Update PaymentController

Edit `uni_ms/src/main/java/com/badrulamin/University_Management/controller/PaymentController.java`:

1. Replace `initiatePayment` method (lines 89-98):

Before:
```java
@PostMapping("/initiate")
public ResponseEntity<Payment> initiatePayment(
        @RequestParam Long invoiceId,
        @RequestParam Long studentId,
        @RequestParam Double amount,
        @RequestParam String paymentMethod,
        @RequestParam(required = false) String notes) {
    return ResponseEntity.ok(paymentService.initiatePayment(invoiceId, studentId, amount, paymentMethod, notes));
}
```

After:
```java
@PostMapping("/initiate")
public ResponseEntity<Payment> initiatePayment(
        @Valid @RequestBody PaymentInitiateRequest request) {
    return ResponseEntity.ok(paymentService.initiatePayment(
        request.getInvoiceId(), request.getStudentId(),
        request.getAmount(), request.getPaymentMethod(), request.getNotes()));
}
```

2. Replace `refundPayment` method (lines 129-137):

Before:
```java
@PostMapping("/{id}/refund")
public ResponseEntity<Refund> refundPayment(
        @PathVariable Long id,
        @RequestParam Double amount,
        @RequestParam String reason,
        @RequestParam(required = false) String approvedBy) {
    return ResponseEntity.ok(paymentService.refundPayment(id, amount, reason, approvedBy));
}
```

After:
```java
@PostMapping("/{id}/refund")
public ResponseEntity<Refund> refundPayment(
        @PathVariable Long id,
        @Valid @RequestBody PaymentRefundRequest request) {
    return ResponseEntity.ok(paymentService.refundPayment(
        id, request.getAmount(), request.getReason(), request.getApprovedBy()));
}
```

---

## Verification

1. Compile: `cd uni_ms && mvn compile -q`
   Expected: BUILD SUCCESS

2. Test validation — negative amount:
   ```bash
   curl -X POST http://localhost:8085/api/payments/initiate \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <token>" \
     -d '{"invoiceId":1,"studentId":1,"amount":-100,"paymentMethod":"CASH"}'
   ```
   Expected: 400 with validation error "Amount must be positive"

3. Test validation — invalid payment method:
   ```bash
   curl -X POST http://localhost:8085/api/payments/initiate \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <token>" \
     -d '{"invoiceId":1,"studentId":1,"amount":100,"paymentMethod":"BITCOIN"}'
   ```
   Expected: 400 with validation error about invalid payment method

---

## Maintenance Note

- If new payment methods are added in the future, update the `@Pattern` regex in `PaymentInitiateRequest`.
- Consider creating a `PaymentMethod` enum and using it instead of String for type safety.
- The `PaymentRefundRequest` should ideally validate that the refund amount doesn't exceed the original payment amount — this requires a service-level check (out of scope for this plan, but noted for future work).

---

## Escape Hatch

If the frontend currently sends `initiatePayment` as query params (not JSON body), the frontend must be updated to send a JSON body instead. Check the Angular service file for `payments/initiate` and update the HTTP call from `params` to `body`.
