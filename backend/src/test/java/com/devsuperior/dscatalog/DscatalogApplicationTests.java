package com.devsuperior.dscatalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Tag("Integration")
class DscatalogApplicationTests {

    @Test
    @DisplayName("Should load application context successfully")
    void contextLoads() {
    }

}
