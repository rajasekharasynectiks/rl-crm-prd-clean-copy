package com.rlabs.crm.payload.request.rbac;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditUserRequest implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<Long> role;
}
