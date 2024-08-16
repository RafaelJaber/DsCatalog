package com.devsuperior.dscatalog.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devsuperior.dscatalog.dto.requests.ProductRequest;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Tag("Integration")
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;
    private ProductRequest productRequest;


    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 99999L;
        countTotalProducts = 25L;
        productRequest = Factory.createProductRequest();
    }

    @Test
    @DisplayName("Should return a sorted page of products when sorted by name")
    public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/products?page=0&size=10&sort=name,asc")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
        result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
    }

    @Test
    @DisplayName("Should return product when ID exists")
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
    @DisplayName("Should persist a new product")
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
    @DisplayName("Should update a product when ID exists")
    public void updateShouldReturnProductResponseWhenIdExists() throws Exception {
        String newName = "NEW_NAME";
        productRequest.setName(newName);
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
        result.andExpect(jsonPath("$.name").value(newName));
    }

    @Test
    @DisplayName("Should return 404 Not Found when updating product with non-existing ID")
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
    @DisplayName("Should throw EntityNotFoundException when deleting a product with non-existing ID")
    public void deleteShouldThrowsEntityNotFoundExceptionWhenIdDoesNotExists() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isNotFound());
    }
}
