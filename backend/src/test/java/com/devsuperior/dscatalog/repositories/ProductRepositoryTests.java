package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
@Tag("Unit")
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = productRepository.count();
    }

    @Test
    @DisplayName("Should return a non-empty Optional when ID exists")
    public void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {
        Optional<Product> product = productRepository.findById(existingId);
        Assertions.assertTrue(product.isPresent());
    }

    @Test
    @DisplayName("Should return an empty Optional when ID does not exist")
    public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist() {
        Optional<Product> product = productRepository.findById(nonExistingId);
        Assertions.assertFalse(product.isPresent());
    }

    @Test
@   DisplayName("Should persist a product with auto-incremented ID when ID is null")
    public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
        Product product = Factory.createProduct();
        product.setId(null);

        Product inserted = productRepository.save(product);

        Assertions.assertNotNull(inserted.getId());
        Assertions.assertEquals(countTotalProducts + 1, inserted.getId());
    }

    @Test
    @DisplayName("Should update an existing product when ID exists")
    public void saveShouldUpdateExistingProductWhenIdExists() {
        Product product = productRepository.findById(existingId).get();

        product.setName("Updated Name");
        productRepository.save(product);

        Product updatedProduct = productRepository.findById(existingId).get();
        Assertions.assertEquals("Updated Name", updatedProduct.getName());
    }

    @Test
    @DisplayName("Should remove a product when ID exists")
    public void deleteShouldRemoveProductWhenIdExists() {

        productRepository.deleteById(existingId);

        Optional<Product> result = productRepository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }


}
