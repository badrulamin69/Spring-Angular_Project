package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rooms")
public class Room extends BaseEntity {

    @NotBlank
    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    private Integer floor;

    @NotNull
    @Column(nullable = false)
    private int capacity;

    @Column(name = "current_occupancy")
    private int currentOccupancy;

    @NotBlank
    @Column(name = "room_type", nullable = false)
    private String roomType;

    @Column(name = "monthly_rent", precision = 10, scale = 2)
    private BigDecimal monthlyRent;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", nullable = false)
    private Hostel hostel;

    @JsonProperty("hostelId")
    public void setHostelId(Long id) {
        if (id != null) {
            this.hostel = new Hostel();
            this.hostel.setId(id);
        }
    }

    @JsonProperty
    public Long getHostelId() {
        return this.hostel != null ? this.hostel.getId() : null;
    }
}
