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
@Table(name = "books")
public class Book extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String author;

    @Column(unique = true)
    private String isbn;

    private String publisher;

    @Column(name = "publication_year")
    private Integer publicationYear;

    private String edition;

    @NotNull
    @Column(name = "total_copies", nullable = false)
    private int totalCopies;

    @NotNull
    @Column(name = "available_copies", nullable = false)
    private int availableCopies;

    @Column(name = "shelf_location")
    private String shelfLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private BookCategory category;

    @JsonProperty("categoryId")
    public void setCategoryId(Long id) {
        if (id != null) {
            this.category = new BookCategory();
            this.category.setId(id);
        }
    }

    @JsonProperty
    public Long getCategoryId() {
        return this.category != null ? this.category.getId() : null;
    }
}
