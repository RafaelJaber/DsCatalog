package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.requests.ProductRequest;
import com.devsuperior.dscatalog.dto.responses.ProductResponse;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
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
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private Category category;


    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 3L;
        dependentId = 4L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));
        category = Factory.createCategory();

        Mockito.when(productRepository.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(page);

        Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(product);

        Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.doNothing().when(productRepository).deleteById(existingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);

        Mockito.when(productRepository.existsById(existingId)).thenReturn(true);
        Mockito.when(productRepository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(productRepository.existsById(dependentId)).thenReturn(true);
    }

    @Test
    @DisplayName("Should return a paginated list of products")
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductResponse> result = productService.findAll(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(productRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should return a product when ID exists")
    public void findByIdShouldReturnProductWhenExistingId() {
        ProductResponse result = productService.findById(existingId);

        Assertions.assertNotNull(result);
        Mockito.verify(productRepository, Mockito.times(1)).findById(existingId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when ID does not exist")
    public void findByIdShouldThrowsExceptionWhenNonExistingId() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            productService.findById(nonExistingId);
        });
        Mockito.verify(productRepository, Mockito.times(1)).findById(nonExistingId);
    }

    @Test
    @DisplayName("Should persist a product successfully")
    public void insertShouldBeAbleToPersistProduct() {
        Assertions.assertDoesNotThrow(() -> {
            ProductRequest request = Factory.createProductRequest();
            productService.insert(request);
        });
        Mockito.verify(productRepository, Mockito.times(1)).save(ArgumentMatchers.any(Product.class));
    }

    @Test
    @DisplayName("Should return a product response when updating an existing product")
    public void updateShouldBeReturnProductResponseWhenIdExists() {
        ProductRequest productRequest = Factory.createProductRequest();

        ProductResponse productResponse = productService.update(productRequest, existingId);

        Assertions.assertNotNull(productResponse);
        Mockito.verify(productRepository, Mockito.times(1)).getReferenceById(existingId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating a non-existent product")
    public void updateShouldBeThrowsExceptionWhenIdDoesNotExist() {
        ProductRequest productRequest = Factory.createProductRequest();

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            productService.update(productRequest, nonExistingId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).getReferenceById(nonExistingId);
    }


    @Test
    @DisplayName("Should delete a product when ID exists")
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            productService.deleteById(existingId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when trying to delete a non-existent product")
    public void deleteShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            productService.deleteById(nonExistingId);
        });

        Mockito.verify(productRepository, Mockito.never()).deleteById(nonExistingId);
    }

    @Test
    @DisplayName("Should throw DatabaseIntegrityException when deleting a product with dependencies")
    public void deleteShouldThrowDatabaseIntegrityExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseIntegrityException.class, () -> {
            productService.deleteById(dependentId);
        });

        Mockito.verify(productRepository).deleteById(dependentId);
    }

}
