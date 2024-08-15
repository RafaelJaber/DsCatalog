package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.requests.CategoryRequest;
import com.devsuperior.dscatalog.dto.responses.CategoryResponse;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        List<Category> categories = categoryRepository.findAll();
        return categories
                .stream()
                .map(CategoryResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category", "id", id.toString())
        );
        return new CategoryResponse(category);
    }

    @Transactional
    public CategoryResponse insert(CategoryRequest request) {
        Category category = getCategory(request);
        category = categoryRepository.save(category);
        return new CategoryResponse(category);
    }


    private static Category getCategory(CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .createdAt(OffsetDateTime.now())
                .build();
    }
}
