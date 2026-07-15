package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "document_verifications")
public class DocumentVerification extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private AdmissionApplication application;

    @NotBlank
    @Column(name = "document_type", nullable = false)
    private String documentType;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "verification_date")
    private LocalDate verificationDate;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @JsonProperty("applicationId")
    public void setApplicationId(Long id) {
        if (id != null) {
            this.application = new AdmissionApplication();
            this.application.setId(id);
        }
    }

    @JsonProperty
    public Long getApplicationId() {
        return this.application != null ? this.application.getId() : null;
    }
}
