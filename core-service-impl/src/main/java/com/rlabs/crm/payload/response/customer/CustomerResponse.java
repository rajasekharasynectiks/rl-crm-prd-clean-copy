package com.rlabs.crm.payload.response.customer;

import com.rlabs.crm.domain.Customer;
import com.rlabs.crm.payload.response.quotation.QuotationResponseV2;
import com.rlabs.crm.util.DateTimeUtil;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long id;
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
    private String createdOn;
    private String createdOnTextForm;
    private String createdBy;
    private String updatedOn;
    private String updatedBy;
    private Boolean favourite;
    private Boolean isNew;
    private Boolean isLead;
    private List<CustomerNoteResponse> notes;
    private String status;
    private List<QuotationResponseV2> quotations;

    public static CustomerResponse buildCustomerResponse(Customer customer, DateTimeUtil dateTimeUtil){
        return CustomerResponse.builder()
            .id(customer.getId())
            .uid(customer.getUid())
            .name(customer.getName())
            .companyName(customer.getCompanyName())
            .companyAbrv(customer.getCompanyAbrv())
            .phoneNo(customer.getPhoneNo())
            .email(customer.getEmail())
            .address(customer.getAddress())
            .city(customer.getCity())
            .zipCode(customer.getZipCode())
            .country(customer.getCountry())
            .state(customer.getState())
            .favourite(customer.getFavourite())
            .isNew(customer.getIsNew())
            .isLead(customer.getIsLead())
            .notes(customer.getNotes() != null ? customer.getNotes().stream().map(CustomerNoteResponse::buildNoteResponse).collect(Collectors.toList()) : null)
            .status(customer.getStatus())
            .createdOn(customer.getCreatedOn() != null ? customer.getCreatedOn().toString() : null)
            .createdOnTextForm(customer.getCreatedOn() != null ? dateTimeUtil.getDateDifferenceInReadableFormat(customer.getCreatedOn(), LocalDateTime.now()) : null)
            .createdBy(customer.getCreatedBy())
            .updatedOn(customer.getUpdatedOn() != null ? customer.getUpdatedOn().toString() : null)
            .updatedBy(customer.getUpdatedBy())
            .quotations(customer.getQuotations() != null ? customer.getQuotations().stream()
                .map(obj -> QuotationResponseV2.buildQuotationResponseV2(obj, dateTimeUtil))
                .collect(Collectors.toList()) : null)
            .build();
    }
}
