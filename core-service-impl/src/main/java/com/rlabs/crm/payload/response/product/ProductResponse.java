package com.rlabs.crm.payload.response.product;

import com.rlabs.crm.domain.Product;
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
public class ProductResponse implements Serializable {
    private Long id;
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
    private List<ProductIngredientResponse> ingredients;
    private List<ProductPackagingDetailResponse> packagingDetails;

    public static ProductResponse buildResponse (Product product){
        if(product == null){
            return null;
        }
        return ProductResponse.builder()
            .id(product.getId())
            .uid(product.getUid())
            .name(product.getName())
            .custPrdNumber(product.getCustPrdNumber())
            .category(product.getCategory())
            .type(product.getType())
            .capsuleSize(product.getCapsuleSize())
            .packagingType(product.getPackagingType())
            .countPerBottle(product.getCountPerBottle())
            .batchSize(product.getBatchSize())
            .dosagePerUnit(product.getDosagePerUnit())
            .dynamicFields(product.getDynamicFields())
            .materialCost(product.getMaterialCost())
            .processLoss(product.getProcessLoss())
            .fillerCost(product.getFillerCost())
            .freightCharges(product.getFreightCharges())
            .markUp(product.getMarkUp())
            .capsuleFillingCost(product.getCapsuleFillingCost())
            .packagingCost(product.getPackagingCost())
            .testingCost(product.getTestingCost())
            .stabilityCost(product.getStabilityCost())
            .fulfillmentCost(product.getFulfillmentCost())
            .build();
    }
}
