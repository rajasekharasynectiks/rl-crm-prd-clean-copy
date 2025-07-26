package com.rlabs.crm.service.customer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rlabs.crm.config.Constants;
import com.rlabs.crm.domain.Customer;
import com.rlabs.crm.domain.CustomerAuditHistory;
import com.rlabs.crm.repository.CustomerAuditHistoryRepository;
import com.rlabs.crm.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CustomerAuditHistoryService {

    @Autowired
    private CustomerAuditHistoryRepository customerAuditHistoryRepository;

    @Autowired
    private MapperUtil mapperUtil;

    public List<CustomerAuditHistory> findAll(){
        return customerAuditHistoryRepository.findAll();
    }

    public List<CustomerAuditHistory> findAllById(List<Long> ids){
        return customerAuditHistoryRepository.findAllById(ids);
    }

    public CustomerAuditHistory findById(Long id){
        return customerAuditHistoryRepository.findById(id).orElse(null);
    }

    public List<CustomerAuditHistory> findByCustomerId(Long customerId){
        log.debug("Getting customer history. Customer id: {}",customerId);
        return customerAuditHistoryRepository.findByCustomerIdOrderByIdDesc(customerId);
    }

    @Transactional
    public CustomerAuditHistory addCustomerAuditHistory(Customer newRequest, Customer oldRequest, String operation) {
        log.debug("Adding customer audit history");

        CustomerAuditHistory customerAuditHistory = CustomerAuditHistory.builder()
            .customerId(newRequest.getId())
            .uid(newRequest.getUid())
            .name(newRequest.getName())
            .companyName(newRequest.getCompanyName())
            .companyAbrv(newRequest.getCompanyAbrv())
            .phoneNo(newRequest.getPhoneNo())
            .email(newRequest.getEmail())
            .address(newRequest.getAddress())
            .city(newRequest.getCity())
            .zipCode(newRequest.getZipCode())
            .country(newRequest.getCountry())
            .favourite(newRequest.getFavourite())
            .isNew(newRequest.getIsNew())
            .isLead(newRequest.getIsLead())
            .status(newRequest.getStatus())
            .profileImageLocation(newRequest.getProfileImageLocation())
            .profileImageAccessUri(newRequest.getProfileImageAccessUri())
            .profileImageFileName(newRequest.getProfileImageFileName())
            .submittedBy(Constants.INSERT.equals(operation) ? newRequest.getCreatedBy() : newRequest.getUpdatedBy())
            .submittedOn(Constants.INSERT.equals(operation) ? newRequest.getCreatedOn() : newRequest.getUpdatedOn())
            .operation(operation)
            .build();

        if(oldRequest != null){
            customerAuditHistory.setChanges(mapperUtil.toJson(getChanges(newRequest, oldRequest)));
        }

        customerAuditHistory = customerAuditHistoryRepository.save(customerAuditHistory);

        log.debug("New quotation audit history added");
        return customerAuditHistory;
    }

    public ArrayNode getChanges(Customer newRequest, Customer oldRequest){
        if(oldRequest == null){
            return null;
        }
        ArrayNode arrayNode = mapperUtil.getMapper().createArrayNode();

        if(!StringUtils.isBlank(newRequest.getName()) && !newRequest.getName().equals(oldRequest.getName())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "Name");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getName());
            objectNode.put(Constants.NEW_VALUE, newRequest.getName());
            arrayNode.add(objectNode);
        }
        if(!StringUtils.isBlank(newRequest.getCompanyName()) && !newRequest.getCompanyName().equals(oldRequest.getCompanyName())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "CompanyName");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getCompanyName());
            objectNode.put(Constants.NEW_VALUE, newRequest.getCompanyName());
            arrayNode.add(objectNode);
        }
        if(!StringUtils.isBlank(newRequest.getCompanyAbrv()) && !newRequest.getCompanyAbrv().equals(oldRequest.getCompanyAbrv())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "CompanyAbrv");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getCompanyAbrv());
            objectNode.put(Constants.NEW_VALUE, newRequest.getCompanyAbrv());
            arrayNode.add(objectNode);
        }
        if(newRequest.getPhoneNo() != null && !newRequest.getPhoneNo().equals(oldRequest.getPhoneNo())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "PhoneNo");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getPhoneNo());
            objectNode.put(Constants.NEW_VALUE, newRequest.getPhoneNo());
            arrayNode.add(objectNode);
        }
        if(newRequest.getEmail() != null && !newRequest.getEmail().equals(oldRequest.getEmail())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "Email");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getEmail());
            objectNode.put(Constants.NEW_VALUE, newRequest.getEmail());
            arrayNode.add(objectNode);
        }
        if(!StringUtils.isBlank(newRequest.getAddress()) && !newRequest.getAddress().equals(oldRequest.getAddress())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "Address");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getAddress());
            objectNode.put(Constants.NEW_VALUE, newRequest.getAddress());
            arrayNode.add(objectNode);
        }
        if(!StringUtils.isBlank(newRequest.getCity()) && !newRequest.getCity().equals(oldRequest.getCity())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "City");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getCity());
            objectNode.put(Constants.NEW_VALUE, newRequest.getCity());
            arrayNode.add(objectNode);
        }
        if(!StringUtils.isBlank(newRequest.getZipCode()) && !newRequest.getZipCode().equals(oldRequest.getZipCode())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "ZipCode");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getZipCode());
            objectNode.put(Constants.NEW_VALUE, newRequest.getZipCode());
            arrayNode.add(objectNode);
        }
        if(!StringUtils.isBlank(newRequest.getCountry()) && !newRequest.getCountry().equals(oldRequest.getCountry())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "Country");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getCountry());
            objectNode.put(Constants.NEW_VALUE, newRequest.getCountry());
            arrayNode.add(objectNode);
        }
        if(newRequest.getFavourite() != null && !newRequest.getFavourite().equals(oldRequest.getFavourite())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "Favourite");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getFavourite());
            objectNode.put(Constants.NEW_VALUE, newRequest.getFavourite());
            arrayNode.add(objectNode);
        }
        if(newRequest.getIsNew() != null && !newRequest.getIsNew().equals(oldRequest.getIsNew())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "IsNew");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getIsNew());
            objectNode.put(Constants.NEW_VALUE, newRequest.getIsNew());
            arrayNode.add(objectNode);
        }
        if(newRequest.getIsLead() != null && !newRequest.getIsLead().equals(oldRequest.getIsLead())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "IsLead");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getIsLead());
            objectNode.put(Constants.NEW_VALUE, newRequest.getIsLead());
            arrayNode.add(objectNode);
        }

        if(!StringUtils.isBlank(newRequest.getStatus()) && !newRequest.getStatus().equals(oldRequest.getStatus())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "Status");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getStatus());
            objectNode.put(Constants.NEW_VALUE, newRequest.getStatus());
            arrayNode.add(objectNode);
        }
        if(!StringUtils.isBlank(newRequest.getProfileImageLocation()) && !newRequest.getProfileImageLocation().equals(oldRequest.getProfileImageLocation())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "ProfileImageLocation");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getProfileImageLocation());
            objectNode.put(Constants.NEW_VALUE, newRequest.getProfileImageLocation());
            arrayNode.add(objectNode);
        }
        if(!StringUtils.isBlank(newRequest.getProfileImageAccessUri()) && !newRequest.getProfileImageAccessUri().equals(oldRequest.getProfileImageAccessUri())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "ProfileImageAccessUri");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getProfileImageAccessUri());
            objectNode.put(Constants.NEW_VALUE, newRequest.getProfileImageAccessUri());
            arrayNode.add(objectNode);
        }
        if(!StringUtils.isBlank(newRequest.getProfileImageFileName()) && !newRequest.getProfileImageFileName().equals(oldRequest.getProfileImageFileName())){
            ObjectNode objectNode = mapperUtil.getMapper().createObjectNode();
            objectNode.put("key", "ProfileImageFileName");
            objectNode.put(Constants.OLD_VALUE, oldRequest.getProfileImageFileName());
            objectNode.put(Constants.NEW_VALUE, newRequest.getProfileImageFileName());
            arrayNode.add(objectNode);
        }
        return arrayNode;
    }


}
