package com.rlabs.crm.payload.response.quotation;

import com.rlabs.crm.config.Constants;
import com.rlabs.crm.domain.Quotation;
import com.rlabs.crm.util.DateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationResponseV2 implements Serializable {
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

    public static QuotationResponseV2 buildQuotationResponseV2(Quotation quotation, DateTimeUtil dateTimeUtil){
        return QuotationResponseV2.builder()
            .id(quotation.getId())
            .uid(quotation.getUid())
            .qtNumber(quotation.getQtNumber())
            .qtOwner(quotation.getQtOwner())
            .status(quotation.getStatus())
            .comments(quotation.getComments())
            .qtExpiryDate(quotation.getQtExpiryDate() != null ? dateTimeUtil.convertLocalDateToString(quotation.getQtExpiryDate(), Constants.DATE_TIME_FORMAT_yyyy_MM_dd) : null)
            .qtDate(quotation.getQtDate() != null ? dateTimeUtil.convertLocalDateToString(quotation.getQtDate(), Constants.DATE_TIME_FORMAT_yyyy_MM_dd) : null)
            .dynamicFields(quotation.getDynamicFields())
            .version(quotation.getVersion())
            .createdOn(quotation.getCreatedOn() != null ? quotation.getCreatedOn().toString() : null)
            .createdOnTextForm(quotation.getCreatedOn() != null ? dateTimeUtil.getDateDifferenceInReadableFormat(quotation.getCreatedOn(), LocalDateTime.now()) : null)
            .createdBy(quotation.getCreatedBy())
            .updatedOn(quotation.getUpdatedOn() != null ? quotation.getUpdatedOn().toString() : null)
            .updatedBy(quotation.getUpdatedBy())
            .build();
    }
}


