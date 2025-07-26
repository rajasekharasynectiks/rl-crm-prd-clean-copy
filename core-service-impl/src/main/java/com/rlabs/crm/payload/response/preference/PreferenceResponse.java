package com.rlabs.crm.payload.response.preference;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PreferenceResponse implements Serializable {
    private Long id;
    private String category;
    private String pfKey ;
    private String pfValue ;
    private String createdOn;
    private String createdOnTextForm;
    private String createdBy;
    private String updatedOn;
    private String updatedBy;
}
