package com.devsuperior.dscatalog.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devsuperior.dscatalog.dto.requests.CategoryRequest;
import com.devsuperior.dscatalog.dto.responses.CategoryResponse;
import com.devsuperior.dscatalog.services.CategoryService;
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

@WebMvcTest(CategoryController.class)
@Tag("Unit")
public class CategoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private CategoryResponse categoryResponse;
    private CategoryRequest categoryRequest;
    private PageImpl<CategoryResponse> page;
    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;

        categoryResponse = Factory.createCategoryResponse();
        categoryRequest = Factory.createCategoryRequest();

        page = new PageImpl<>(List.of(categoryResponse));
        Mockito.when(categoryService.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(page);

        Mockito.when(categoryService.findById(existingId)).thenReturn(categoryResponse);
        Mockito.when(categoryService.findById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(categoryService.insert(ArgumentMatchers.any(CategoryRequest.class))).thenReturn(categoryResponse);

        Mockito.when(categoryService.update(Mockito.any(), Mockito.eq(existingId))).thenReturn(categoryResponse);
        Mockito.when(categoryService.update(Mockito.any(), Mockito.eq(nonExistingId))).thenThrow(EntityNotFoundException.class);

        Mockito.doNothing().when(categoryService).deleteById(existingId);
        Mockito.doThrow(EntityNotFoundException.class).when(categoryService).deleteById(nonExistingId);
        Mockito.doThrow(DatabaseIntegrityException.class).when(categoryService).deleteById(dependentId);
    }

    @Test
    @DisplayName("Should return a page of categories")
    public void findAllShouldReturnPage() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/categories")
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return a category when ID exists")
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
    @DisplayName("Should create a new category and return it")
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
    @DisplayName("Should update a category and return the updated category when ID exists")
    public void updateShouldReturnCategoryResponseWhenIdExists() throws Exception {
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
    }

    @Test
    @DisplayName("Should return 404 Not Found when updating a category with non-existing ID")
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
    @DisplayName("Should delete a category when ID exists")
    public void deleteShouldDoNothingWhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/categories/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 404 Not Found when deleting a category with non-existing ID")
    public void deleteShouldThrowEntityNotFoundExceptionWhenIdDoesNotExists() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/categories/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when deleting a category with dependent entities")
    public void deleteShouldThrowDatabaseIntegrityExceptionWhenIdIsDependent() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/categories/{id}", dependentId)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isBadRequest());
    }
}
