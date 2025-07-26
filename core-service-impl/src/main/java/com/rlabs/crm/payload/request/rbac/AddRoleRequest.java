package com.rlabs.crm.payload.request.rbac;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddRoleRequest implements Serializable {

    private String name;
    private String description;
    private List<AddPermissionRequest> permissions;
}
