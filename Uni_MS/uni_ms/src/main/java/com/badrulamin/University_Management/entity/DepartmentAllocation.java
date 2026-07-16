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
@Table(name = "department_allocation")
public class DepartmentAllocation extends BaseEntity {

    @NotBlank
    @Column(name = "allocation_number", unique = true, nullable = false)
    private String allocationNumber;

    @Column(name = "merit_rank")
    private Integer meritRank;

    @Column(name = "total_score")
    private Double totalScore;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "allocated_at")
    private LocalDateTime allocatedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Size(max = 2000)
    private String remarks;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registration_id", nullable = false)
    private PreAdmissionRegistration registration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "allocated_program_id")
    private Program allocatedProgram;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "allocated_department_id")
    private Department allocatedDepartment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "allocated_batch_id")
    private Batch allocatedBatch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "allocated_section_id")
    private Section allocatedSection;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "allocated_by_id")
    private User allocatedBy;

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

    @JsonProperty("allocatedProgramId")
    public Long getAllocatedProgramId() {
        return allocatedProgram != null ? allocatedProgram.getId() : null;
    }

    @JsonProperty("allocatedProgramId")
    public void setAllocatedProgramId(Long programId) {
        if (programId != null) {
            Program p = new Program();
            p.setId(programId);
            this.allocatedProgram = p;
        }
    }

    @JsonProperty("allocatedDepartmentId")
    public Long getAllocatedDepartmentId() {
        return allocatedDepartment != null ? allocatedDepartment.getId() : null;
    }

    @JsonProperty("allocatedDepartmentId")
    public void setAllocatedDepartmentId(Long departmentId) {
        if (departmentId != null) {
            Department d = new Department();
            d.setId(departmentId);
            this.allocatedDepartment = d;
        }
    }

    @JsonProperty("allocatedBatchId")
    public Long getAllocatedBatchId() {
        return allocatedBatch != null ? allocatedBatch.getId() : null;
    }

    @JsonProperty("allocatedBatchId")
    public void setAllocatedBatchId(Long batchId) {
        if (batchId != null) {
            Batch b = new Batch();
            b.setId(batchId);
            this.allocatedBatch = b;
        }
    }

    @JsonProperty("allocatedSectionId")
    public Long getAllocatedSectionId() {
        return allocatedSection != null ? allocatedSection.getId() : null;
    }

    @JsonProperty("allocatedSectionId")
    public void setAllocatedSectionId(Long sectionId) {
        if (sectionId != null) {
            Section s = new Section();
            s.setId(sectionId);
            this.allocatedSection = s;
        }
    }

    @JsonProperty("semesterId")
    public Long getSemesterId() {
        return semester != null ? semester.getId() : null;
    }

    @JsonProperty("semesterId")
    public void setSemesterId(Long semesterId) {
        if (semesterId != null) {
            Semester s = new Semester();
            s.setId(semesterId);
            this.semester = s;
        }
    }

    @JsonProperty("allocatedById")
    public Long getAllocatedById() {
        return allocatedBy != null ? allocatedBy.getId() : null;
    }

    @JsonProperty("allocatedById")
    public void setAllocatedById(Long userId) {
        if (userId != null) {
            User u = new User();
            u.setId(userId);
            this.allocatedBy = u;
        }
    }
}
