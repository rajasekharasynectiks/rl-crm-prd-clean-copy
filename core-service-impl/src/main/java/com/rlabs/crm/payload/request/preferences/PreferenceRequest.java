package com.rlabs.crm.payload.request.preferences;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PreferenceRequest implements Serializable {
    private Long id;
    private String category;
    private String pfKey ;
    private String pfValue ;
}
