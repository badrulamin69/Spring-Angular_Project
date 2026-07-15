package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "administration_divisions")
public class AdministrationDivision extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String code;

    private String description;

    private String deanName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_id")
    private Campus campus;

    @JsonProperty("campusId")
    public void setCampusId(Long id) {
        if (id != null) {
            this.campus = new Campus();
            this.campus.setId(id);
        }
    }

    @JsonProperty
    public Long getCampusId() {
        return this.campus != null ? this.campus.getId() : null;
    }

    @Column(nullable = false)
    private boolean isActive = true;
}
