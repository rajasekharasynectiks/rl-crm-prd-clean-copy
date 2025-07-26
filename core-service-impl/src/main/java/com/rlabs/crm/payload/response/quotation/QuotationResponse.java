package com.rlabs.crm.payload.response.quotation;

import com.rlabs.crm.config.Constants;
import com.rlabs.crm.domain.Quotation;
import com.rlabs.crm.payload.response.customer.CustomerResponseV2;
import com.rlabs.crm.payload.response.product.ProductIngredientResponse;
import com.rlabs.crm.payload.response.product.ProductPackagingDetailResponse;
import com.rlabs.crm.payload.response.product.ProductResponse;
import com.rlabs.crm.util.DateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationResponse implements Serializable {
    private Long id;
    private String uid;
    private String qtNumber;
    private String qtOwner;
    private String status;
    private String comments;
    private String qtExpiryDate;
    private String qtDate;
    private String dynamicFields;
    private Integer version;
    private String createdOn;
    private String createdOnTextForm;
    private String createdBy;
    private String updatedOn;
    private String updatedBy;
    private CustomerResponseV2 customer;
    private ProductResponse product;

    public static QuotationResponse buildQuotationResponse(Quotation quotation, DateTimeUtil dateTimeUtil){
        CustomerResponseV2 customer = CustomerResponseV2.buildResponse(quotation.getCustomer(), dateTimeUtil);
        ProductResponse product = ProductResponse.buildResponse(quotation.getProduct());
        if(quotation.getProduct() != null && quotation.getProduct().getIngredients() != null
                && !quotation.getProduct().getIngredients().isEmpty()){
            List<ProductIngredientResponse> productIngredients = quotation.getProduct().getIngredients().stream().map(ProductIngredientResponse::buildProductIngredient).collect(Collectors.toList());
            product.setIngredients(productIngredients);
        }
        if(quotation.getProduct() != null && quotation.getProduct().getProductPackagingDetails() != null
            && !quotation.getProduct().getProductPackagingDetails().isEmpty()){
            List<ProductPackagingDetailResponse> packagingDetailResponses = quotation.getProduct().getProductPackagingDetails().stream().map(ProductPackagingDetailResponse::buildProductPackagingDetail).collect(Collectors.toList());
            product.setPackagingDetails(packagingDetailResponses);
        }
        return QuotationResponse.builder()
            .id(quotation.getId())
            .uid(quotation.getUid())
            .qtNumber(quotation.getQtNumber())
            .qtOwner(quotation.getQtOwner())
            .status(quotation.getStatus())
            .qtExpiryDate(quotation.getQtExpiryDate() != null ? dateTimeUtil.convertLocalDateToString(quotation.getQtExpiryDate(), Constants.DATE_TIME_FORMAT_yyyy_MM_dd) : null)
            .qtDate(quotation.getQtDate() != null ? dateTimeUtil.convertLocalDateToString(quotation.getQtDate(), Constants.DATE_TIME_FORMAT_yyyy_MM_dd) : null)
            .dynamicFields(quotation.getDynamicFields())
            .version(quotation.getVersion())
            .comments(quotation.getComments())
            .customer(customer)
            .product(product)
            .createdOn(quotation.getCreatedOn() != null ? quotation.getCreatedOn().toString() : null)
            .createdOnTextForm(quotation.getCreatedOn() != null ? dateTimeUtil.getDateDifferenceInReadableFormat(quotation.getCreatedOn(), LocalDateTime.now()) : null)
            .createdBy(quotation.getCreatedBy())
            .updatedOn(quotation.getUpdatedOn() != null ? quotation.getUpdatedOn().toString() : null)
            .updatedBy(quotation.getUpdatedBy())
            .build();
    }
}
