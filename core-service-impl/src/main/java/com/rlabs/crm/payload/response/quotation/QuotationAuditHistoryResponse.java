package com.rlabs.crm.payload.response.quotation;

import com.rlabs.crm.config.Constants;
import com.rlabs.crm.domain.QuotationAuditHistory;
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
public class QuotationAuditHistoryResponse implements Serializable {

    private Long id;
    private Long quotationId;
    private String uid;
    private String qtNumber;
    private String qtOwner;
    private String status;
    private String qtExpiryDate;
    private String qtDate;
    private String dynamicFields;
    private Integer version;
    private String comments;
    private String auditComments;
    private String operation;
    private String submittedBy;
    private String submittedOn;
    private String submittedOnTextForm;
    private String changes;

    public static QuotationAuditHistoryResponse buildQuotationAuditHistoryResponse(QuotationAuditHistory quotationAuditHistory, DateTimeUtil dateTimeUtil){
        return QuotationAuditHistoryResponse.builder()
            .id(quotationAuditHistory.getId())
            .quotationId(quotationAuditHistory.getQuotationId())
            .uid(quotationAuditHistory.getUid())
            .qtNumber(quotationAuditHistory.getQtNumber())
            .qtOwner(quotationAuditHistory.getQtOwner())
            .status(quotationAuditHistory.getStatus())
            .qtExpiryDate(quotationAuditHistory.getQtExpiryDate() != null ? dateTimeUtil.convertLocalDateToString(quotationAuditHistory.getQtExpiryDate(), Constants.DATE_TIME_FORMAT_yyyy_MM_dd) : null)
            .qtDate(quotationAuditHistory.getQtDate() != null ? dateTimeUtil.convertLocalDateToString(quotationAuditHistory.getQtDate(), Constants.DATE_TIME_FORMAT_yyyy_MM_dd) : null)
            .dynamicFields(quotationAuditHistory.getDynamicFields())
            .version(quotationAuditHistory.getVersion())
            .comments(quotationAuditHistory.getComments())
            .auditComments(quotationAuditHistory.getAuditComments())
            .operation(quotationAuditHistory.getOperation())
            .submittedBy(quotationAuditHistory.getSubmittedBy())
            .submittedOn(quotationAuditHistory.getSubmittedOn() != null ? quotationAuditHistory.getSubmittedOn().toString() : null)
            .submittedOnTextForm(quotationAuditHistory.getSubmittedOn() != null ? dateTimeUtil.getDateDifferenceInReadableFormat(quotationAuditHistory.getSubmittedOn(), LocalDateTime.now()) : null)
            .changes(quotationAuditHistory.getChanges())
            .build();
    }
}
