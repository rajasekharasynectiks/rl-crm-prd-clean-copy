package com.rlabs.crm.payload.response.rbac;

import com.rlabs.crm.domain.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllRoleResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean isDefault;
    private Collection<? extends Permission> permissions;
    private Collection<? extends UserResp> users;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResp{
        private Long id;
        private String username;
        private String firstName;
        private String lastName;
    }
}

