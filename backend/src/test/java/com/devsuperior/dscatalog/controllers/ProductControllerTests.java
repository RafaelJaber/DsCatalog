package com.devsuperior.dscatalog.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devsuperior.dscatalog.dto.requests.ProductRequest;
import com.devsuperior.dscatalog.dto.responses.ProductResponse;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseIntegrityException;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;


@WebMvcTest(ProductController.class)
@Tag("Unit")
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductResponse productResponse;
    private ProductRequest productRequest;
    private PageImpl<ProductResponse> page;
    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;

        productResponse = Factory.createProductResponse();
        productRequest = Factory.createProductRequest();

        page = new PageImpl<>(List.of(productResponse));
        Mockito.when(productService.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(page);

        Mockito.when(productService.findById(existingId)).thenReturn(productResponse);
        Mockito.when(productService.findById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(productService.insert(ArgumentMatchers.any(ProductRequest.class))).thenReturn(productResponse);

        Mockito.when(productService.update(Mockito.any(), Mockito.eq(existingId))).thenReturn(productResponse);
        Mockito.when(productService.update(Mockito.any(), Mockito.eq(nonExistingId))).thenThrow(EntityNotFoundException.class);

        Mockito.doNothing().when(productService).deleteById(existingId);
        Mockito.doThrow(EntityNotFoundException.class).when(productService).deleteById(nonExistingId);
        Mockito.doThrow(DatabaseIntegrityException.class).when(productService).deleteById(dependentId);
    }

    @Test
    @DisplayName("Should return a page of products")
    public void findAllShouldReturnPage() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return a product when ID exists")
    public void findByIdShouldReturnProductWhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existingId));
    }

    @Test
    @DisplayName("Should return 404 Not Found when ID does not exist")
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.path").value(String.format("/products/%s", nonExistingId)));
    }

    @Test
    @DisplayName("Should create a new product and return it")
    public void insertShouldBeAbleToPersistProduct() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productRequest);
        ResultActions result =
                mockMvc.perform(post("/products", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    @DisplayName("Should update a product and return the updated product when ID exists")
    public void updateShouldReturnProductResponseWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productRequest);
        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingId)
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    @DisplayName("Should return 404 Not Found when updating a product with non-existing ID")
    public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productRequest);
        ResultActions result =
                mockMvc.perform(put("/products/{id}", nonExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.path").value(String.format("/products/%s", nonExistingId)));
    }

    @Test
    @DisplayName("Should delete a product when ID exists")
    public void deleteShouldDoNothingWhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 404 Not Found when deleting a product with non-existing ID")
    public void deleteShouldThrowsEntityNotFoundExceptionWhenIdDoesNotExists() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when deleting a product with dependent entities")
    public void deleteShouldThrowsDatabaseIntegrityExceptionWhenIdDoesNotExists() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/products/{id}", dependentId)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isBadRequest());
    }
}
