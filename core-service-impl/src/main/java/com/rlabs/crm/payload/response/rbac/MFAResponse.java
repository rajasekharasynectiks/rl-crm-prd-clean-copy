package com.rlabs.crm.payload.response.rbac;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MFAResponse {
    private String mfaKey;
    private Object mfa;
}
