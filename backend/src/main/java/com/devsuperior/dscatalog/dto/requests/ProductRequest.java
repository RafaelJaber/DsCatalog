package com.devsuperior.dscatalog.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @Positive(message = "Price must be a positive value")
    private Double price;

    private String imgUrl;

    @PastOrPresent(message = "Date must be in the past or present")
    private OffsetDateTime date;

    List<CategoryProductRequest> categories = new ArrayList<>();
}
