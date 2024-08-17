package com.devsuperior.dscatalog.controllers;

import com.devsuperior.dscatalog.dto.requests.ProductRequest;
import com.devsuperior.dscatalog.dto.responses.ProductResponse;
import com.devsuperior.dscatalog.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> findAll(Pageable pageable) {
        Page<ProductResponse> products = productService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        ProductResponse product = productService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductResponse> insert(@Valid @RequestBody ProductRequest requestDto) {
        ProductResponse inserted = productService.insert(requestDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(inserted.getId()).toUri();
        return ResponseEntity.created(uri).body(inserted);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest requestDto
    ) {
        ProductResponse updated = productService.update(requestDto, id);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
