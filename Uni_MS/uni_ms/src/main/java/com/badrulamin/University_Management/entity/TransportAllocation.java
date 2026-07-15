package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
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
@Table(name = "transport_allocations")
public class TransportAllocation extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "pickup_point")
    private String pickupPoint;

    @Column(name = "drop_point")
    private String dropPoint;

    @Column(name = "monthly_fee", precision = 10, scale = 2)
    private BigDecimal monthlyFee;

    private String status;

    @JsonProperty("studentId")
    public void setStudentId(Long id) {
        if (id != null) {
            this.student = new Student();
            this.student.setId(id);
        }
    }

    @JsonProperty
    public Long getStudentId() {
        return this.student != null ? this.student.getId() : null;
    }

    @JsonProperty("routeId")
    public void setRouteId(Long id) {
        if (id != null) {
            this.route = new Route();
            this.route.setId(id);
        }
    }

    @JsonProperty
    public Long getRouteId() {
        return this.route != null ? this.route.getId() : null;
    }

    @JsonProperty("vehicleId")
    public void setVehicleId(Long id) {
        if (id != null) {
            this.vehicle = new Vehicle();
            this.vehicle.setId(id);
        }
    }

    @JsonProperty
    public Long getVehicleId() {
        return this.vehicle != null ? this.vehicle.getId() : null;
    }
}
