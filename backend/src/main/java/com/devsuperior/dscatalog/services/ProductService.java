package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.requests.CategoryProductRequest;
import com.devsuperior.dscatalog.dto.requests.ProductRequest;
import com.devsuperior.dscatalog.dto.responses.ProductResponse;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.projections.ProductProjection;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseIntegrityException;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(ProductResponse::new);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product", "id", id.toString())
        );
        return new ProductResponse(product, product.getCategories());
    }

    @Transactional
    public ProductResponse insert(ProductRequest request) {
        Product product = new Product();
        copyDtoToEntity(request, product);

        Product inserted = productRepository.save(product);
        return new ProductResponse(inserted, product.getCategories());
    }

    @Transactional
    public ProductResponse update(ProductRequest request, Long id) {
        try {
            Product product = productRepository.getReferenceById(id);
            copyDtoToEntity(request, product);

            Product updated = productRepository.save(product);
            return new ProductResponse(updated, updated.getCategories());
        } catch (jakarta.persistence.EntityNotFoundException e) {
            throw new EntityNotFoundException("Product", "id", id.toString());
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteById(Long id) {
        if(!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product", "id", id.toString());
        }
        try {
            productRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DatabaseIntegrityException();
        }
    }

    private void copyDtoToEntity(ProductRequest dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.setDate(dto.getDate());

        entity.getCategories().clear();
        for (CategoryProductRequest catReq : dto.getCategories()) {
            Category category = categoryRepository.getReferenceById(catReq.getId());
            entity.getCategories().add(category);
        }
    }

    public Page<ProductResponse> findAllProductProjection(String categoryId, String name, Pageable pageable) {

        List<Long> categoryIds = Arrays.asList();

        if (!"0".equals(categoryId)) {
            categoryIds = Arrays.stream(categoryId.split(",")).map(Long::parseLong).toList();
        }

        Page<ProductProjection> page = productRepository.searchProducts(categoryIds ,name, pageable);
        List<Long> productIds = page.map(ProductProjection::getId).toList();

        List<Product> entities = productRepository.searchProductsWithCategories(productIds);
        List<ProductResponse> responses = entities.stream()
                .map(p -> new ProductResponse(p, p.getCategories()))
                .toList();

        return new PageImpl<>(responses, page.getPageable(), page.getTotalElements());
    }
}
