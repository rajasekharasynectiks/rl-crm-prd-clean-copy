package com.rlabs.crm.service.quotation;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rlabs.crm.config.Constants;
import com.rlabs.crm.domain.*;
import com.rlabs.crm.repository.QuotationAuditHistoryRepository;
import com.rlabs.crm.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class QuotationAuditHistoryService {

    @Autowired
    private QuotationAuditHistoryRepository quotationAuditHistoryRepository;

    @Autowired
    private MapperUtil mapperUtil;

    public List<QuotationAuditHistory> findAll(){
        return quotationAuditHistoryRepository.findAll();
    }

    public List<QuotationAuditHistory> findAllById(List<Long> ids){
        return quotationAuditHistoryRepository.findAllById(ids);
    }

    public QuotationAuditHistory findById(Long id){
        return quotationAuditHistoryRepository.findById(id).orElse(null);
    }

    public List<QuotationAuditHistory> findAllById(Long quotationId){
        return quotationAuditHistoryRepository.findByQuotationIdOrderByIdDesc(quotationId);
    }

    @Transactional
    public QuotationAuditHistory addQuotationAuditHistory(Quotation newRequest, Quotation oldRequest, String operation) {
        log.debug("Adding quotation in quotation audit history");

        QuotationAuditHistory quotationAuditHistory = QuotationAuditHistory.builder()
            .quotationId(newRequest.getId())
            .uid(newRequest.getUid())
            .qtNumber(newRequest.getQtNumber())
            .qtOwner(newRequest.getQtOwner())
            .status(newRequest.getStatus())
            .qtExpiryDate(newRequest.getQtExpiryDate())
            .qtDate(newRequest.getQtDate())
            .dynamicFields(newRequest.getDynamicFields())
            .version(newRequest.getVersion())
            .comments(newRequest.getComments())
            .submittedBy(Constants.INSERT.equals(operation) ? newRequest.getCreatedBy() : newRequest.getUpdatedBy())
            .submittedOn(Constants.INSERT.equals(operation) ? newRequest.getCreatedOn() : newRequest.getUpdatedOn())
            .operation(operation)
            .build();
        if(Constants.STATUS_DRAFT.equalsIgnoreCase(newRequest.getStatus())){
            quotationAuditHistory.setAuditComments("Saved As Draft");
        } else if (Constants.STATUS_PENDING_FOR_REVIEW.equalsIgnoreCase(newRequest.getStatus())) {
            quotationAuditHistory.setAuditComments("Quotation Submitted For Review");
        }else if (Constants.STATUS_REJECTED.equalsIgnoreCase(newRequest.getStatus())) {
            quotationAuditHistory.setAuditComments("Quotation Rejected by Reviewer");
        }else if (Constants.STATUS_APPROVED.equalsIgnoreCase(newRequest.getStatus())) {
            quotationAuditHistory.setAuditComments("Quotation Approved by Reviewer");
        }
        if(oldRequest != null){
            quotationAuditHistory.setChanges(mapperUtil.toJson(getChanges(newRequest, oldRequest)));
        }

        quotationAuditHistory = quotationAuditHistoryRepository.save(quotationAuditHistory);

        log.debug("New quotation audit history added");
        return quotationAuditHistory;
    }

    public ArrayNode getChanges(Quotation newRequest, Quotation oldRequest){
        if(oldRequest == null){
            return null;
        }
        ArrayNode arrayNode = mapperUtil.getMapper().createArrayNode();

        if(!StringUtils.isBlank(newRequest.getQtNumber()) && !newRequest.getQtNumber().equals(oldRequest.getQtNumber())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "QuotationNo");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getQtNumber());
            objectNode.put(Constants.NEW_VALUE, newRequest.getQtNumber());
            arrayNode.add(objectNode);
        }
        if(!StringUtils.isBlank(newRequest.getQtOwner()) && !newRequest.getQtOwner().equals(oldRequest.getQtOwner())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "QuotationOwner");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getQtOwner());
            objectNode.put(Constants.NEW_VALUE, newRequest.getQtOwner());
            arrayNode.add(objectNode);
        }
        if(!StringUtils.isBlank(newRequest.getStatus()) && !newRequest.getStatus().equals(oldRequest.getStatus())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "Status");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getStatus());
            objectNode.put(Constants.NEW_VALUE, newRequest.getStatus());
            arrayNode.add(objectNode);
        }
        if(newRequest.getQtDate() != null && !newRequest.getQtDate().equals(oldRequest.getQtDate())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "QuotationDate");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getQtDate().toString());
            objectNode.put(Constants.NEW_VALUE, newRequest.getQtDate().toString());
            arrayNode.add(objectNode);
        }
        if(newRequest.getQtExpiryDate() != null && !newRequest.getQtExpiryDate().equals(oldRequest.getQtExpiryDate())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "QuotationExpiryDate");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getQtExpiryDate().toString());
            objectNode.put(Constants.NEW_VALUE, newRequest.getQtExpiryDate().toString());
            arrayNode.add(objectNode);
        }
        if(!StringUtils.isBlank(newRequest.getComments()) && !newRequest.getComments().equals(oldRequest.getComments())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "Comments");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getComments());
            objectNode.put(Constants.NEW_VALUE, newRequest.getComments());
            arrayNode.add(objectNode);
        }
        return arrayNode;
    }

    public List<QuotationAuditHistory> findByQuotationId(Long quotationId){
        log.debug("Getting quotation history. Quotation id: {}",quotationId);
        return quotationAuditHistoryRepository.findByQuotationIdOrderByIdDesc(quotationId);
    }
}
