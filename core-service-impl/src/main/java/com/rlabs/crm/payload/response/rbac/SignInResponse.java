package com.rlabs.crm.payload.response.rbac;

import com.rlabs.crm.config.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponse {
    private Long id;
    private String username;
    private String email;
    private boolean mfaEnabled;
    private boolean mfaEnforced;
    private boolean enabled;
    private Integer loginCount;
    private String lastLoginAt;
    private String createdOn;
    private String firstName;
    private String lastName;
    private String token;
    private String type = Constants.BEARER;
    private Collection<? extends RoleResponse> roles;
}
