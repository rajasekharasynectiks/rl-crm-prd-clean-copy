package com.rlabs.crm.payload.response.rbac;

import com.rlabs.crm.domain.Permission;
import lombok.*;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean isDefault;
    private Collection<? extends Permission> permissions;

}

