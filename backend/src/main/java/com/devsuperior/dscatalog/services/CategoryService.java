package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.requests.CategoryRequest;
import com.devsuperior.dscatalog.dto.responses.CategoryResponse;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseIntegrityException;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> findAll(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(CategoryResponse::new);
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
        Category category = new Category();
        copyDtoToEntity(request, category);

        Category inserted = categoryRepository.save(category);
        return new CategoryResponse(inserted);
    }

    @Transactional
    public CategoryResponse update(CategoryRequest request, Long id) {
        try {
            Category category = categoryRepository.getReferenceById(id);
            copyDtoToEntity(request, category);

            Category updated = categoryRepository.save(category);
            return new CategoryResponse(updated);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            throw new EntityNotFoundException("Category", "id", id.toString());
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteById(Long id) {
        if(!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category", "id", id.toString());
        }
        try {
            categoryRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DatabaseIntegrityException();
        }
    }


    private static void copyDtoToEntity(CategoryRequest dto, Category entity) {
       entity.setName(dto.getName());
    }
}
