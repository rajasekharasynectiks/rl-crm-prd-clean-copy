package com.rlabs.crm.payload.response.product;

import com.rlabs.crm.domain.ProductPackagingDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPackagingDetailResponse implements Serializable {
    private Long id;
    private String packagingType;
    private String details;


    public static ProductPackagingDetailResponse buildProductPackagingDetail(ProductPackagingDetail request){
        return ProductPackagingDetailResponse.builder()
            .id(request.getId())
            .packagingType(request.getPackagingType())
            .details(request.getDetails())
            .build();

    }
}
