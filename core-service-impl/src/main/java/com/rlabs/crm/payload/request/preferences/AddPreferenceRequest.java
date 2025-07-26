package com.rlabs.crm.payload.request.preferences;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AddPreferenceRequest implements Serializable {
    private String category;
    private String pfKey ;
    private String pfValue ;
}
