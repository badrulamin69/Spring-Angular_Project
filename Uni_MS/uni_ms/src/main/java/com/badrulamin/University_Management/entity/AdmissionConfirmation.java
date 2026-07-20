package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admission_confirmation")
public class AdmissionConfirmation extends BaseEntity {

    @Size(max = 50)
    @Column(name = "confirmation_number", unique = true, nullable = false)
    private String confirmationNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "allocation_id", nullable = false)
    private DepartmentAllocation allocation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registration_id", nullable = false)
    private PreAdmissionRegistration registration;

    @Size(max = 30)
    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "documents_submitted")
    private Boolean documentsSubmitted = false;

    @Column(name = "documents_verified")
    private Boolean documentsVerified = false;

    @Column(name = "documents_verified_by")
    private Long documentsVerifiedBy;

    @Column(name = "documents_verified_at")
    private LocalDateTime documentsVerifiedAt;

    @Size(max = 2000)
    @Column(name = "document_remarks")
    private String documentRemarks;

    @Column(name = "fee_paid")
    private Boolean feePaid = false;

    @Column(name = "fee_amount")
    private Double feeAmount;

    @Column(name = "fee_payment_method")
    private String feePaymentMethod;

    @Column(name = "fee_transaction_id")
    private String feeTransactionId;

    @Column(name = "fee_paid_at")
    private LocalDateTime feePaidAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "confirmed_by")
    private Long confirmedBy;

    @Size(max = 2000)
    private String remarks;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id")
    private AcademicSession session;

    @JsonProperty("allocationId")
    public Long getAllocationId() {
        return allocation != null ? allocation.getId() : null;
    }

    @JsonProperty("allocationId")
    public void setAllocationId(Long allocationId) {
        if (allocationId != null) {
            DepartmentAllocation a = new DepartmentAllocation();
            a.setId(allocationId);
            this.allocation = a;
        }
    }

    @JsonProperty("registrationId")
    public Long getRegistrationId() {
        return registration != null ? registration.getId() : null;
    }

    @JsonProperty("registrationId")
    public void setRegistrationId(Long registrationId) {
        if (registrationId != null) {
            PreAdmissionRegistration r = new PreAdmissionRegistration();
            r.setId(registrationId);
            this.registration = r;
        }
    }

    @JsonProperty("sessionId")
    public Long getSessionId() {
        return session != null ? session.getId() : null;
    }

    @JsonProperty("sessionId")
    public void setSessionId(Long sessionId) {
        if (sessionId != null) {
            AcademicSession s = new AcademicSession();
            s.setId(sessionId);
            this.session = s;
        }
    }
}
