package com.rlabs.crm.payload.request.rbac;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest  {
    private String username;
    private String oldPassword;
    private String newPassword;
}
