package com.devsuperior.dscatalog.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    private List<RoleRequest> roles;
}
