package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "generated_reports")
public class GeneratedReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private ReportTemplate template;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String reportType;

    @Column(columnDefinition = "JSON")
    private String parameters;

    private String fileUrl;

    private String format;

    @Column(name = "generated_by")
    private Long generatedBy;

    private LocalDateTime generatedAt;

    @JsonProperty("templateId")
    public void setTemplateId(Long id) {
        if (id != null) {
            this.template = new ReportTemplate();
            this.template.setId(id);
        }
    }

    @JsonProperty
    public Long getTemplateId() {
        return this.template != null ? this.template.getId() : null;
    }
}
