package com.devsuperior.dscatalog.dto.requests;

import com.devsuperior.dscatalog.services.validation.UserInsertValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@UserInsertValid
@EqualsAndHashCode(callSuper = true)
public class UserInsertRequest extends UserRequest {

    @NotBlank(message = "Field required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

}
