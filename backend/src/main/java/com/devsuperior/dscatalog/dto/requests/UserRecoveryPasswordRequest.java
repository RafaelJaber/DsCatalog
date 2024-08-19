package com.devsuperior.dscatalog.dto.requests;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRecoveryPasswordRequest {

    @NotBlank(message = "Field required")
    @Email(message = "Invalid e-mail")
    private String email;
}
