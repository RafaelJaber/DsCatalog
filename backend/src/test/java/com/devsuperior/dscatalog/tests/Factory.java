package com.devsuperior.dscatalog.tests;

import com.devsuperior.dscatalog.dto.requests.CategoryProductRequest;
import com.devsuperior.dscatalog.dto.requests.CategoryRequest;
import com.devsuperior.dscatalog.dto.requests.ProductRequest;
import com.devsuperior.dscatalog.dto.responses.CategoryResponse;
import com.devsuperior.dscatalog.dto.responses.ProductResponse;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;

public class Factory {

    public static Product createProduct() {
        Product product = Product.builder()
                .id(1L)
                .name("Phone")
                .description("Good Phone")
                .price(800.0)
                .imgUrl("https://img.com/img.png")
                .date(OffsetDateTime.now())
                .categories(new HashSet<>())
                .build();
        product.getCategories().add(
                Category.builder()
                        .id(1L)
                        .name("Electronics")
                        .build()
        );
        return product;
    }

    public static ProductResponse createProductResponse() {
        Product product = createProduct();
        return new ProductResponse(product, product.getCategories());
    }

    public static ProductRequest createProductRequest(Product product) {
        ProductRequest productRequest = ProductRequest.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imgUrl(product.getImgUrl())
                .date(product.getDate())
                .categories(new ArrayList<>())
                .build();
        for (Category category : product.getCategories()) {
            CategoryProductRequest categoryProductRequest = CategoryProductRequest.builder()
                    .id(category.getId())
                    .build();
            productRequest.getCategories().add(categoryProductRequest);
        }
        return productRequest;
    }

    public static ProductRequest createProductRequest() {
        Product product = createProduct();
        return createProductRequest(product);
    }

    public static Category createCategory() {
        return Category.builder()
                .id(1L)
                .name("Electronics")
                .build();
    }

    public static CategoryResponse createCategoryResponse() {
        Category category = createCategory();
        return new CategoryResponse(category);
    }

    public static CategoryRequest createCategoryRequest(Category category) {
        return CategoryRequest.builder()
                .name(category.getName())
                .build();
    }

    public static CategoryRequest createCategoryProductRequest() {
        Category category = createCategory();
        return createCategoryRequest(category);
    }
}
