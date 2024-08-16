package com.devsuperior.dscatalog.tests;

import com.devsuperior.dscatalog.dto.responses.ProductResponse;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

import java.time.OffsetDateTime;
import java.util.HashSet;

public class Factory {

    public static Product createProduct() {
        Product product = Product.builder()
                .name("Phone")
                .description("Good Phone")
                .price(800.0)
                .imgUrl("https://img.com/img.png")
                .date(OffsetDateTime.now())
                .categories(new HashSet<>())
                .build();
        product.getCategories().add(
                Category.builder()
                        .id(2L)
                        .name("Electronics")
                        .build()
        );
        return product;
    }

    public static ProductResponse createProductResponse() {
        Product product = createProduct();
        return new ProductResponse(product, product.getCategories());
    }
}
