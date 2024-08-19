package com.devsuperior.dscatalog.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewPasswordRequest {

    @NotBlank(message = "Field required")
    private String token;

    @NotBlank(message = "Field required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}
