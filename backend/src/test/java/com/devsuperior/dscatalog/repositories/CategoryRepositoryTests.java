package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
@Tag("Unit")
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository categoryRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalCategories;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalCategories = categoryRepository.count();
    }

    @Test
    @DisplayName("Should return a non-empty Optional when ID exists")
    public void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {
        Optional<Category> category = categoryRepository.findById(existingId);
        Assertions.assertTrue(category.isPresent());
    }

    @Test
    @DisplayName("Should return an empty Optional when ID does not exist")
    public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist() {
        Optional<Category> category = categoryRepository.findById(nonExistingId);
        Assertions.assertFalse(category.isPresent());
    }

    @Test
    @DisplayName("Should persist a category with auto-incremented ID when ID is null")
    public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
        Category category = Factory.createCategory();
        category.setId(null);

        Category inserted = categoryRepository.save(category);

        Assertions.assertNotNull(inserted.getId());
        Assertions.assertEquals(countTotalCategories + 1, inserted.getId());
    }

    @Test
    @DisplayName("Should update an existing category when ID exists")
    public void saveShouldUpdateExistingCategoryWhenIdExists() {
        Category category = categoryRepository.findById(existingId).get();

        category.setName("Updated Name");
        categoryRepository.save(category);

        Category updatedCategory = categoryRepository.findById(existingId).get();
        Assertions.assertEquals("Updated Name", updatedCategory.getName());
    }

    @Test
    @DisplayName("Should remove a category when ID exists")
    public void deleteShouldRemoveCategoryWhenIdExists() {
        categoryRepository.deleteById(existingId);

        Optional<Category> result = categoryRepository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }
}
