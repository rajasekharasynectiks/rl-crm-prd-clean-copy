package com.rlabs.crm.payload.response.rbac;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordResponse {
    private Long id;
    private String username;
    private String email;
    private boolean isPasswordChange = false;
}
