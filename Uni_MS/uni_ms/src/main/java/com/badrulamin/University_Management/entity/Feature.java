package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "features", uniqueConstraints = {
    @UniqueConstraint(columnNames = "feature_key")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feature extends BaseEntity {

    @NotBlank
    @Column(name = "feature_key", nullable = false, unique = true, length = 200)
    private String featureKey;

    @NotBlank
    @Column(name = "feature_name", nullable = false, length = 200)
    private String featureName;

    @Column(name = "module_name", nullable = false, length = 100)
    private String moduleName;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private Boolean isEnabled = true;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;
}
