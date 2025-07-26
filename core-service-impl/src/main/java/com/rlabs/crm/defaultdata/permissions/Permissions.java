package com.rlabs.crm.defaultdata.permissions;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Permissions implements Serializable {
    private String category;
    private List<String> permission;
}
