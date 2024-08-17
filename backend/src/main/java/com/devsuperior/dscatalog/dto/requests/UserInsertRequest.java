package com.devsuperior.dscatalog.dto.requests;

import com.devsuperior.dscatalog.services.validation.UserInsertValid;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@UserInsertValid
@EqualsAndHashCode(callSuper = true)
public class UserInsertRequest extends UserRequest {
    private String password;

}
