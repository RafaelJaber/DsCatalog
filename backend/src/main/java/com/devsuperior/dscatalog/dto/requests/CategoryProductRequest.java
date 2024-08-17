package com.devsuperior.dscatalog.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryProductRequest {

    @NotNull(message = "Category Product ID cannot be null")
    private Long id;
}
