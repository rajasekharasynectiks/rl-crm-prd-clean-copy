package com.rlabs.crm.payload.response.customer;

import com.rlabs.crm.domain.CustomerNote;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerNoteResponse  {

    private Long id;
    private String notes;

    public static CustomerNoteResponse buildNoteResponse(CustomerNote customerNote){
        return CustomerNoteResponse.builder()
            .id(customerNote.getId())
            .notes(customerNote.getNotes())
            .build();
    }
}
