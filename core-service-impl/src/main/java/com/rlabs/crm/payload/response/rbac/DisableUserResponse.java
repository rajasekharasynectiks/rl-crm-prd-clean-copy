package com.rlabs.crm.payload.response.rbac;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisableUserResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean mfaEnabled;
    private boolean enabled;
    private Integer loginCount;
    private String lastLoginAt;
    private String createdOn;
    private Collection<? extends RoleResponse> roles;
}
