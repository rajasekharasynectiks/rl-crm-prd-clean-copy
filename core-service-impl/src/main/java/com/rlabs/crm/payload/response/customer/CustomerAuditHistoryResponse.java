package com.rlabs.crm.payload.response.customer;

import com.rlabs.crm.domain.CustomerAuditHistory;
import com.rlabs.crm.util.DateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAuditHistoryResponse {

    private Long id;
    private Long customerId;
    private String uid;
    private String name;
    private String companyName;
    private String companyAbrv;
    private String phoneNo;
    private String email;
    private String address;
    private String city;
    private String zipCode;
    private String country;
    private String state;
    private Boolean favourite;
    private Boolean isNew;
    private Boolean isLead;
    private String status;
    private String profileImageLocation;
    private String profileImageAccessUri;
    private String profileImageFileName;
    private String submittedBy;
    private String submittedOn;
    private String operation;
    private String changes;

    public static CustomerAuditHistoryResponse buildResponse(CustomerAuditHistory req, DateTimeUtil dateTimeUtil){
        return CustomerAuditHistoryResponse.builder()
            .id(req.getId())
            .customerId(req.getCustomerId())
            .uid(req.getUid())
            .name(req.getName())
            .companyName(req.getCompanyName())
            .companyAbrv(req.getCompanyAbrv())
            .phoneNo(req.getPhoneNo())
            .email(req.getEmail())
            .address(req.getAddress())
            .city(req.getCity())
            .zipCode(req.getZipCode())
            .country(req.getCountry())
            .state(req.getState())
            .favourite(req.getFavourite())
            .isNew(req.getIsNew())
            .isLead(req.getIsLead())
            .status(req.getStatus())
            .profileImageLocation(req.getProfileImageLocation())
            .profileImageAccessUri(req.getProfileImageAccessUri())
            .profileImageFileName(req.getProfileImageFileName())
            .submittedBy(req.getSubmittedBy())
            .submittedOn(req.getSubmittedOn() != null ? req.getSubmittedOn().toString() : null)
            .operation(req.getOperation())
            .changes(req.getChanges())
            .build();
    }
}
