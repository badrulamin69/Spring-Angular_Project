package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dashboard_widgets")
public class DashboardWidget extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @NotBlank
    @Column(name = "widget_title", nullable = false)
    private String widgetTitle;

    @NotBlank
    @Column(name = "widget_type", nullable = false)
    private String widgetType;

    @Column(name = "widget_config", columnDefinition = "TEXT")
    private String widgetConfig;

    @Column(name = "api_endpoint")
    private String apiEndpoint;

    @Column(name = "order_no")
    private Long orderNo = 0l;

    @Column(name = "column_span")
    private Integer columnSpan = 1;

    @Column(name = "data_source")
    private String dataSource;

    private Boolean visible = true;

    @JsonProperty("roleId")
    public void setRoleId(Long id) {
        if (id != null) {
            this.role = new Role();
            this.role.setId(id);
        }
    }

    @JsonProperty
    public Long getRoleId() {
        return this.role != null ? this.role.getId() : null;
    }
}
