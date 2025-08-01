package com.rlabs.crm.payload.request.quotation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewQuotationRequest implements Serializable {
    private Long id;
    private String status;
    private String comments;
}
