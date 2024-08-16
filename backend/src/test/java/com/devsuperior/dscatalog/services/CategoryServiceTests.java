package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.requests.CategoryRequest;
import com.devsuperior.dscatalog.dto.responses.CategoryResponse;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.CategoryRepositoryTests;
import com.devsuperior.dscatalog.services.exceptions.DatabaseIntegrityException;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@Tag("Unit")
public class CategoryServiceTests {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private PageImpl<Category> page;
    private Category category;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 3L;
        dependentId = 4L;
        category = Factory.createCategory();
        page = new PageImpl<>(List.of(category));

        Mockito.when(categoryRepository.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(page);

        Mockito.when(categoryRepository.findById(existingId)).thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(categoryRepository.save(ArgumentMatchers.any(Category.class))).thenReturn(category);

        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.doNothing().when(categoryRepository).deleteById(existingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(categoryRepository).deleteById(dependentId);

        Mockito.when(categoryRepository.existsById(existingId)).thenReturn(true);
        Mockito.when(categoryRepository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(categoryRepository.existsById(dependentId)).thenReturn(true);
    }

    @Test
    @DisplayName("Should return a paginated list of categories")
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<CategoryResponse> result = categoryService.findAll(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(categoryRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should return a category when ID exists")
    public void findByIdShouldReturnCategoryWhenExistingId() {
        CategoryResponse result = categoryService.findById(existingId);

        Assertions.assertNotNull(result);
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(existingId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when ID does not exist")
    public void findByIdShouldThrowsExceptionWhenNonExistingId() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.findById(nonExistingId);
        });
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(nonExistingId);
    }

    @Test
    @DisplayName("Should persist a category successfully")
    public void insertShouldBeAbleToPersistCategory() {
        Assertions.assertDoesNotThrow(() -> {
            CategoryRequest request = Factory.createCategoryRequest();
            categoryService.insert(request);
        });
        Mockito.verify(categoryRepository, Mockito.times(1)).save(ArgumentMatchers.any(Category.class));
    }

    @Test
    @DisplayName("Should return a category response when updating an existing category")
    public void updateShouldBeReturnCategoryResponseWhenIdExists() {
        CategoryRequest categoryRequest = Factory.createCategoryRequest();

        CategoryResponse categoryResponse = categoryService.update(categoryRequest, existingId);

        Assertions.assertNotNull(categoryResponse);
        Mockito.verify(categoryRepository, Mockito.times(1)).getReferenceById(existingId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating a non-existent category")
    public void updateShouldBeThrowsExceptionWhenIdDoesNotExist() {
        CategoryRequest categoryRequest = Factory.createCategoryRequest();

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.update(categoryRequest, nonExistingId);
        });

        Mockito.verify(categoryRepository, Mockito.times(1)).getReferenceById(nonExistingId);
    }

    @Test
    @DisplayName("Should delete a category when ID exists")
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            categoryService.deleteById(existingId);
        });

        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when trying to delete a non-existent category")
    public void deleteShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.deleteById(nonExistingId);
        });

        Mockito.verify(categoryRepository, Mockito.never()).deleteById(nonExistingId);
    }

    @Test
    @DisplayName("Should throw DatabaseIntegrityException when deleting a category with dependencies")
    public void deleteShouldThrowDatabaseIntegrityExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseIntegrityException.class, () -> {
            categoryService.deleteById(dependentId);
        });

        Mockito.verify(categoryRepository).deleteById(dependentId);
    }
}
