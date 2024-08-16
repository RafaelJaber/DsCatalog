package com.devsuperior.dscatalog.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devsuperior.dscatalog.dto.requests.ProductRequest;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
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
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

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
}
