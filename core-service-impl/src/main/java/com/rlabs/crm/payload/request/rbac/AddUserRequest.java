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
public class AddUserRequest implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<Long> role;

}
