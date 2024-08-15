package com.devsuperior.dscatalog.controllers;

import com.devsuperior.dscatalog.dto.requests.CategoryRequest;
import com.devsuperior.dscatalog.dto.responses.CategoryResponse;
import com.devsuperior.dscatalog.services.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> findAll(Pageable pageable) {
        Page<CategoryResponse> list = categoryService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        CategoryResponse categoryResponse = categoryService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(categoryResponse);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> insert(@RequestBody CategoryRequest requestDto) {
        CategoryResponse inserted = categoryService.insert(requestDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(inserted.getId()).toUri();
        return ResponseEntity.created(uri).body(inserted);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @RequestBody CategoryRequest requestDto
    ) {
        CategoryResponse updated = categoryService.update(requestDto, id);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
