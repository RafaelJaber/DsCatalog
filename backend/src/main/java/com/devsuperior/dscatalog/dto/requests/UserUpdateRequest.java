package com.devsuperior.dscatalog.dto.requests;

import com.devsuperior.dscatalog.services.validation.UserUpdateValid;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@UserUpdateValid
@EqualsAndHashCode(callSuper = true)
public class UserUpdateRequest extends UserRequest{
}
