package com.rlabs.crm.payload.request.rbac;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisableMfaRequest {
    private String username;
    private Integer token;
}
