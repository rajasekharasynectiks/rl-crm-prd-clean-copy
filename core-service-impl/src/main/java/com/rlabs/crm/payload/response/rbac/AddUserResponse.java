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
public class AddUserResponse {
    private Long id;
    private String username;
    private String email;
    private boolean isEnabled;
    private boolean isMfaEnabled;
    private String firstName;
    private String lastName;
    private boolean isDefault;
    private Collection<? extends RoleResponse> roles;
}
