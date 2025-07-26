package com.rlabs.crm.payload.request.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProductPackagingDetailRequest implements Serializable {
    private String packagingType;
    private String details;
}
