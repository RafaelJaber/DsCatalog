package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.requests.ProductRequest;
import com.devsuperior.dscatalog.dto.responses.ProductResponse;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;
    private ProductRequest productRequest;


    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 99999L;
        countTotalProducts = productRepository.count();
        productRequest = Factory.createProductRequest();
    }

    @Test
    public void findAllPagedShouldReturnPageWhenPage0Size10() {
        int pageNumber = 0;
        int pageSize = 10;

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<ProductResponse> result = productService.findAll(pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(pageNumber, result.getNumber());
        Assertions.assertEquals(pageSize, result.getSize());
        Assertions.assertEquals(countTotalProducts, result.getTotalElements());
    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWHenPageDoesNotExist() {
        int pageNumber = 50;
        int pageSize = 25;

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<ProductResponse> result = productService.findAll(pageRequest);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhenSortByName() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortBy = "name";

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));

        Page<ProductResponse> result = productService.findAll(pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Macbook Pro", result.getContent().getFirst().getName());
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() {
        ProductResponse result = productService.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    public void findByIdShouldThrowEntityNotFoundExceptionWhenIdDoesExists() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            productService.findById(nonExistingId);
        });
    }

    @Test
    public void insertShouldPersistProduct() {
        ProductResponse result = productService.insert(productRequest);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(countTotalProducts + 1, productRepository.count());
    }

    @Test
    public void updateShouldBeReturnProductResponseWhenIdExists() {
        String newName = "UPDATED_NAME";

        productRequest.setName(newName);
        ProductResponse productResponse = productService.update(productRequest, existingId);

        Assertions.assertNotNull(productResponse);
        Assertions.assertEquals(existingId, productResponse.getId());
        Assertions.assertEquals(newName, productResponse.getName());
    }

    @Test
    public void updateShouldBeThrowsExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> {
            productService.update(productRequest, nonExistingId);
        });
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {
        productService.deleteById(existingId);

        Assertions.assertEquals(countTotalProducts - 1, productRepository.count());
    }

    @Test
    public void deleteShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            productService.deleteById(nonExistingId);
        });
    }
}
