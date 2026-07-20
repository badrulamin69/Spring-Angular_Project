package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admission_document")
public class AdmissionDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "confirmation_id", nullable = false)
    private AdmissionConfirmation confirmation;

    @NotBlank
    @Size(max = 100)
    @Column(name = "document_type", nullable = false)
    private String documentType;

    @NotBlank
    @Size(max = 200)
    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Size(max = 500)
    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_size")
    private Long fileSize;

    @Size(max = 50)
    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Size(max = 500)
    private String remarks;

    @JsonProperty("confirmationId")
    public Long getConfirmationId() {
        return confirmation != null ? confirmation.getId() : null;
    }

    @JsonProperty("confirmationId")
    public void setConfirmationId(Long confirmationId) {
        if (confirmationId != null) {
            AdmissionConfirmation c = new AdmissionConfirmation();
            c.setId(confirmationId);
            this.confirmation = c;
        }
    }
}
