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
@Table(name = "announcements")
public class Announcement extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by_id")
    private User postedBy;

    private Boolean isActive = true;

    @JsonProperty("postedById")
    public void setPostedById(Long id) {
        if (id != null) {
            this.postedBy = new User();
            this.postedBy.setId(id);
        }
    }

    @JsonProperty
    public Long getPostedById() {
        return this.postedBy != null ? this.postedBy.getId() : null;
    }
}
