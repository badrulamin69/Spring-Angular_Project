package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menus")
public class Menu extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String title;

    private String icon;

    private String route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Menu parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("orderNo ASC")
    private List<Menu> children;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Column(name = "permission_code")
    private String permissionCode;

    @Column(nullable = false)
    private String module;

    @Column(nullable = false)
    private Boolean visible = true;

    @Column(nullable = false)
    private Boolean active = true;

    @JsonProperty("parentId")
    public void setParentId(Long id) {
        if (id != null) {
            this.parent = new Menu();
            this.parent.setId(id);
        }
    }

    @JsonProperty
    public Long getParentId() {
        return this.parent != null ? this.parent.getId() : null;
    }
}
