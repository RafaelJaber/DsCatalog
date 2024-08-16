package com.devsuperior.dscatalog.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devsuperior.dscatalog.dto.requests.CategoryRequest;
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
public class CategoryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalCategories;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 99999L;
        countTotalCategories = 3L;
        categoryRequest = Factory.createCategoryRequest();
    }

    @Test
    @DisplayName("Should return a sorted page of categories when sorted by name")
    public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/categories?page=0&size=10&sort=name,asc")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.totalElements").value(countTotalCategories));
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].name").value("Computadores"));
        result.andExpect(jsonPath("$.content[1].name").value("Eletrônicos"));
        result.andExpect(jsonPath("$.content[2].name").value("Livros"));
    }

    @Test
    @DisplayName("Should return category when ID exists")
    public void findByIdShouldReturnCategoryWhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/categories/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existingId));
    }

    @Test
    @DisplayName("Should return 404 Not Found when ID does not exist")
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/categories/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.path").value(String.format("/categories/%s", nonExistingId)));
    }

    @Test
    @DisplayName("Should persist a new category")
    public void insertShouldBeAbleToPersistCategory() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(categoryRequest);
        ResultActions result =
                mockMvc.perform(post("/categories")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
    }

    @Test
    @DisplayName("Should update a category when ID exists")
    public void updateShouldReturnCategoryResponseWhenIdExists() throws Exception {
        String newName = "NEW_NAME";
        categoryRequest.setName(newName);
        String jsonBody = objectMapper.writeValueAsString(categoryRequest);
        ResultActions result =
                mockMvc.perform(put("/categories/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.name").value(newName));
    }

    @Test
    @DisplayName("Should return 404 Not Found when updating category with non-existing ID")
    public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(categoryRequest);
        ResultActions result =
                mockMvc.perform(put("/categories/{id}", nonExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.path").value(String.format("/categories/%s", nonExistingId)));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting a category with non-existing ID")
    public void deleteShouldThrowEntityNotFoundExceptionWhenIdDoesNotExists() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/categories/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isNotFound());
    }
}
