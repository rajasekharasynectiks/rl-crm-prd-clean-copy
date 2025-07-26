package com.rlabs.crm.payload.response.product;

import com.rlabs.crm.domain.ProductIngredient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductIngredientResponse implements Serializable {
    private Long id;
    private String rmId;
    private String activeIngredient;
    private String label;
    private String units ;
    private String perDosage ;
    private String qtyUnit ;
    private BigDecimal pricePerUnit ;
    private BigDecimal cost ;


    public static ProductIngredientResponse buildProductIngredient(ProductIngredient request){
        ProductIngredientResponse ingredientResponse = ProductIngredientResponse.builder()
            .id(request.getId())
            .rmId(request.getRmId())
            .activeIngredient(request.getActiveIngredient())
            .label(request.getLabel())
            .units(request.getUnits())
            .perDosage(request.getPerDosage())
            .qtyUnit(request.getQtyUnit())
            .pricePerUnit(request.getPricePerUnit())
            .cost(request.getCost())
            .build();
        return ingredientResponse;
    }
}
