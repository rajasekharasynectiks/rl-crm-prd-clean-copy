package com.rlabs.crm.payload.request.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProductRequest implements Serializable {
    private String uid;
    private String name;
    private String custPrdNumber;
    private String category ;
    private String type ;
    private String capsuleSize ;
    private String packagingType ;
    private Integer countPerBottle ;
    private Integer batchSize ;
    private Integer dosagePerUnit ;
    private BigDecimal materialCost ;
    private BigDecimal processLoss ;
    private BigDecimal fillerCost ;
    private BigDecimal freightCharges ;
    private BigDecimal markUp ;
    private BigDecimal capsuleFillingCost ;
    private BigDecimal packagingCost ;
    private BigDecimal testingCost ;
    private BigDecimal stabilityCost ;
    private BigDecimal fulfillmentCost ;
    private String dynamicFields;
    private List<AddProductIngredientRequest> addProductIngredientRequests;
    private List<AddProductPackagingDetailRequest> addProductPackagingDetailRequests;
}
