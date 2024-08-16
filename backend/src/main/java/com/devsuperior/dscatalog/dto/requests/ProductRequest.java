package com.devsuperior.dscatalog.dto.requests;

import com.devsuperior.dscatalog.dto.responses.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imgUrl;
    private OffsetDateTime date;

    List<CategoryProductRequest> categories = new ArrayList<>();
}
