package com.devsuperior.dscatalog.dto.responses;

import com.devsuperior.dscatalog.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private Long id;
    private String authority;

    public RoleResponse(Role role) {
        this.id = role.getId();
        this.authority = role.getAuthority();
    }
}
