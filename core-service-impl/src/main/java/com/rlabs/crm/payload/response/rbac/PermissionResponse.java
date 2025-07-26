package com.rlabs.crm.payload.response.rbac;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponse {
    private String category;
    private String name;
}
