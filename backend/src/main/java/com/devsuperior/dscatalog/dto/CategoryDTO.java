package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private OffsetDateTime createdAt;

    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.createdAt = category.getCreatedAt();
    }
}
