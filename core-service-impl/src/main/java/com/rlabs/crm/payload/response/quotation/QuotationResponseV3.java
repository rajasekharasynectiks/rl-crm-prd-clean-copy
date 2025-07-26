package com.rlabs.crm.payload.response.quotation;

import com.rlabs.crm.config.Constants;
import com.rlabs.crm.domain.Document;
import com.rlabs.crm.domain.Quotation;
import com.rlabs.crm.payload.response.customer.CustomerResponseV2;
import com.rlabs.crm.payload.response.document.DocumentResponse;
import com.rlabs.crm.payload.response.product.ProductIngredientResponse;
import com.rlabs.crm.payload.response.product.ProductPackagingDetailResponse;
import com.rlabs.crm.payload.response.product.ProductResponse;
import com.rlabs.crm.service.quotation.QuotationService;
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
public class QuotationResponseV3 implements Serializable {
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
    private List<DocumentResponse> documents;

    public static QuotationResponseV3 buildQuotationResponseV3(Quotation quotation, DateTimeUtil dateTimeUtil, QuotationService quotationService){
        CustomerResponseV2 customer = CustomerResponseV2.buildResponse(quotation.getCustomer(), dateTimeUtil);
        ProductResponse product = ProductResponse.buildResponse(quotation.getProduct());
        List<ProductIngredientResponse> productIngredients = quotation.getProduct().getIngredients().stream().map(ProductIngredientResponse::buildProductIngredient).collect(Collectors.toList());
        product.setIngredients(productIngredients);
        List<ProductPackagingDetailResponse> packagingDetailResponses = quotation.getProduct().getProductPackagingDetails().stream().map(ProductPackagingDetailResponse::buildProductPackagingDetail).collect(Collectors.toList());
        product.setPackagingDetails(packagingDetailResponses);
        List<Document> documentList = quotationService.searchQuotationDocuments(null, quotation.getId(), null, null);
        return QuotationResponseV3.builder()
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
            .documents(documentList.stream().map(DocumentResponse::buildDocumentResponse).collect(Collectors.toList()))
            .createdOn(quotation.getCreatedOn() != null ? quotation.getCreatedOn().toString() : null)
            .createdOnTextForm(quotation.getCreatedOn() != null ? dateTimeUtil.getDateDifferenceInReadableFormat(quotation.getCreatedOn(), LocalDateTime.now()) : null)
            .createdBy(quotation.getCreatedBy())
            .updatedOn(quotation.getUpdatedOn() != null ? quotation.getUpdatedOn().toString() : null)
            .updatedBy(quotation.getUpdatedBy())
            .build();

    }
}

