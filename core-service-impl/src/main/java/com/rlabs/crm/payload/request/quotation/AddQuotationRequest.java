package com.rlabs.crm.payload.request.quotation;

import com.rlabs.crm.payload.request.product.AddProductRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddQuotationRequest implements Serializable {
    private String qtNumber;
    private String qtOwner;
    private String status;
    private String qtExpiryDate;
    private String qtDate;
    private String dynamicFields;
    private Long customerId;
    private AddProductRequest addProductRequest;
}
