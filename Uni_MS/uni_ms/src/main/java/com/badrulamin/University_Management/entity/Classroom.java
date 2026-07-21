package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "classrooms")
public class Classroom extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @JsonProperty("buildingId")
    public void setBuildingId(Long id) {
        if (id != null) {
            this.building = new Building();
            this.building.setId(id);
        }
    }

    @JsonProperty
    public Long getBuildingId() {
        return this.building != null ? this.building.getId() : null;
    }

    @NotBlank
    @Column(nullable = false)
    private String roomNumber;

    @Column(nullable = false)
    private Integer floor = 0;

    @NotNull
    @Column(nullable = false)
    private Integer capacity = 40;

    @NotBlank
    @Column(nullable = false)
    private String roomType = "LECTURE_HALL";

    @Column(nullable = false)
    private boolean isLab = false;

    @Column(nullable = false)
    private boolean isSmartClassroom = false;

    @Column(nullable = false)
    private boolean hasProjector = false;

    @Column(nullable = false)
    private boolean hasWhiteboard = true;

    @Column(nullable = false)
    private boolean hasWifi = true;

    private String equipment;

    @Column(nullable = false)
    private boolean isAvailable = true;

    @Column(nullable = false)
    private boolean isActive = true;

    private String remarks;
}
