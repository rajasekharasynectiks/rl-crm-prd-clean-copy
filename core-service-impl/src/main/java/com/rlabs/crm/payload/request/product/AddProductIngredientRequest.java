package com.rlabs.crm.payload.request.product;

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
public class AddProductIngredientRequest implements Serializable {
    private String rmId;
    private String activeIngredient;
    private String label;
    private String units ;
    private String perDosage ;
    private String qtyUnit ;
    private BigDecimal pricePerUnit ;
    private BigDecimal cost ;

}
