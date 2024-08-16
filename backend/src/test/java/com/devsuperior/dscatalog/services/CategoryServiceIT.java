package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.requests.CategoryRequest;
import com.devsuperior.dscatalog.dto.responses.CategoryResponse;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Tag("Integration")
public class CategoryServiceIT {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalCategories;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 99999L;
        countTotalCategories = categoryRepository.count();
        categoryRequest = Factory.createCategoryRequest();
    }

    @Test
    @DisplayName("Should return a page of categories when page is 0 and size is 10")
    public void findAllPagedShouldReturnPageWhenPage0Size10() {
        int pageNumber = 0;
        int pageSize = 10;

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<CategoryResponse> result = categoryService.findAll(pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(pageNumber, result.getNumber());
        Assertions.assertEquals(pageSize, result.getSize());
        Assertions.assertEquals(countTotalCategories, result.getTotalElements());
    }

    @Test
    @DisplayName("Should return an empty page when the page number does not exist")
    public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {
        int pageNumber = 50;
        int pageSize = 25;

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<CategoryResponse> result = categoryService.findAll(pageRequest);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return a sorted page of categories when sorted by name")
    public void findAllPagedShouldReturnSortedPageWhenSortByName() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortBy = "name";

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));

        Page<CategoryResponse> result = categoryService.findAll(pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Computadores", result.getContent().get(0).getName());
        Assertions.assertEquals("Eletrônicos", result.getContent().get(1).getName());
        Assertions.assertEquals("Livros", result.getContent().get(2).getName());
    }

    @Test
    @DisplayName("Should return a category when ID exists")
    public void findByIdShouldReturnCategoryWhenIdExists() {
        CategoryResponse result = categoryService.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when ID does not exist")
    public void findByIdShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.findById(nonExistingId);
        });
    }

    @Test
    @DisplayName("Should persist a new category")
    public void insertShouldPersistCategory() {
        CategoryResponse result = categoryService.insert(categoryRequest);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(countTotalCategories + 1, categoryRepository.count());
    }

    @Test
    @DisplayName("Should update a category and return the response when ID exists")
    public void updateShouldReturnCategoryResponseWhenIdExists() {
        String newName = "UPDATED_NAME";

        categoryRequest.setName(newName);
        CategoryResponse categoryResponse = categoryService.update(categoryRequest, existingId);

        Assertions.assertNotNull(categoryResponse);
        Assertions.assertEquals(existingId, categoryResponse.getId());
        Assertions.assertEquals(newName, categoryResponse.getName());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating a category with non-existing ID")
    public void updateShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.update(categoryRequest, nonExistingId);
        });
    }

    @Test
    @DisplayName("Should delete a category when ID exists")
    public void deleteShouldDeleteResourceWhenIdExists() {
        CategoryResponse categoryResponse = categoryService.insert(categoryRequest);

        categoryService.deleteById(categoryResponse.getId());

        Assertions.assertEquals(countTotalCategories, categoryRepository.count());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting a category with non-existing ID")
    public void deleteShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.deleteById(nonExistingId);
        });
    }
}
