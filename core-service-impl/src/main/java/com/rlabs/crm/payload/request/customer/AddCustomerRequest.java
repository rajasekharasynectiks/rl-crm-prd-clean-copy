package com.rlabs.crm.payload.request.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddCustomerRequest implements Serializable {
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
    private Boolean isLead;
    private String notes;
}
