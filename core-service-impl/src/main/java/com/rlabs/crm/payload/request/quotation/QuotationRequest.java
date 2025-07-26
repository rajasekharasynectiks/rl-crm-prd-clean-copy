package com.rlabs.crm.payload.request.quotation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedHashMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationRequest implements Serializable {
    private Long id;
    private String qtNumber;
    private String qtOwner;
    private String status;
    private String qtExpiryDate;
    private String qtDate;
    private String dynamicFields;
    private Long customerId;
}
